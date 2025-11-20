package org.nishgrid.clienterp.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.nishgrid.clienterp.dto.JewelryExchangeRequestDTO;
import org.nishgrid.clienterp.dto.ExchangeLogRecordDTO;
import org.nishgrid.clienterp.model.*;
import org.nishgrid.clienterp.repository.SalesExchangeRepository;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.nishgrid.clienterp.repository.SalesItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeService {

    private static final BigDecimal EXCHANGE_FEE_PERCENT = new BigDecimal("0.10");
    private static final int DECIMAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Autowired private SalesExchangeRepository exchangeRepository;
    @Autowired private SalesInvoiceRepository invoiceRepository;
    @Autowired private SalesItemRepository itemRepository;

    @Transactional
    public SalesExchange processJewelryExchange(JewelryExchangeRequestDTO dto) {

        SalesInvoice originalInvoice = invoiceRepository.findById(dto.getOriginalInvoiceId())
                .orElseThrow(() -> new EntityNotFoundException("Original Invoice not found."));

        SalesItem returnedItem = itemRepository.findById(dto.getReturnItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item to be returned not found."));

        SalesItem newItem = itemRepository.findById(dto.getNewItemId())
                .orElseThrow(() -> new EntityNotFoundException("New Item not found."));

        BigDecimal intrinsicCredit = calculateIntrinsicCredit(returnedItem, dto);

        BigDecimal calculatedFinalAdjustment = newItem.getTotalPrice().subtract(intrinsicCredit);

        if (dto.getCustomerFinalAdjustmentAmount().compareTo(calculatedFinalAdjustment.setScale(DECIMAL_SCALE, ROUNDING_MODE)) != 0) {
            throw new IllegalArgumentException("Exchange calculation mismatch. Frontend adjustment: " + dto.getCustomerFinalAdjustmentAmount()
                    + ", Backend calculation: " + calculatedFinalAdjustment.setScale(DECIMAL_SCALE, ROUNDING_MODE));
        }

        SalesExchange exchange = new SalesExchange();
        exchange.setOriginalInvoice(originalInvoice);
        exchange.setReturnedItem(returnedItem);
        exchange.setNewItemId(dto.getNewItemId());

        // Setting audit and financial fields using the new model properties
        exchange.setGoldRateUsed(dto.getGoldRatePerGram24K());
        exchange.setPlatinumRateUsed(dto.getPlatinumRatePerGramPure());
        exchange.setCalculatedIntrinsicCredit(intrinsicCredit);
        exchange.setCustomerFinalAdjustment(dto.getCustomerFinalAdjustmentAmount());

        exchange.setExchangeDate(dto.getExchangeDate().atStartOfDay());
        exchange.setRemarks(dto.getRemarks());
        exchange.setHandledBy(dto.getHandledBy());
        exchange.setCustomer(originalInvoice.getCustomer()); // Assuming customer is retrieved from invoice

        originalInvoice.setStatus("Partially Exchanged");
        invoiceRepository.save(originalInvoice);

        return exchangeRepository.save(exchange);
    }

    private BigDecimal calculateIntrinsicCredit(SalesItem returnedItem, JewelryExchangeRequestDTO dto) {
        BigDecimal totalMeltValue = BigDecimal.ZERO;
        BigDecimal totalGemstoneValue = BigDecimal.ZERO;

        // --- A. Metal Melt Value Calculation (Using SalesItem fields) ---
        try {
            // Assume purity is stored as a karat string (e.g., "916" for 22K, or "950" for Platinum)
            BigDecimal purityValue = new BigDecimal(returnedItem.getPurity());
            BigDecimal netWeight = BigDecimal.valueOf(returnedItem.getNetWeight());
            BigDecimal rate = dto.getGoldRatePerGram24K(); // Defaulting to Gold rate for simplicity
            BigDecimal purityRatio;

            // Simple logic for Gold (assuming purity is x/1000)
            if (purityValue.compareTo(new BigDecimal("1000")) <= 0) {
                purityRatio = purityValue.divide(new BigDecimal("1000"), 4, ROUNDING_MODE);
            } else {
                // Safety for 24K pure gold
                purityRatio = BigDecimal.ONE;
            }

            totalMeltValue = netWeight
                    .multiply(purityRatio)
                    .multiply(rate)
                    .setScale(DECIMAL_SCALE, ROUNDING_MODE);

        } catch (Exception e) {
            // Handle cases where weight/purity might be missing or invalid
            System.err.println("Skipping metal value calculation due to invalid data: " + e.getMessage());
        }

        // --- B. Gemstone Value (Fixed Value) ---
        // Using diamondAmount as the fixed exchange value for the stone component
        totalGemstoneValue = returnedItem.getDiamondAmount() != null
                ? returnedItem.getDiamondAmount() : BigDecimal.ZERO;

        // --- C. Calculate Credit Before Fees ---
        BigDecimal creditBeforeFee = totalMeltValue.add(totalGemstoneValue);

        // --- D. Apply Policy-Based Exchange Fee (10%) ---
        BigDecimal exchangeFee = creditBeforeFee.multiply(EXCHANGE_FEE_PERCENT).setScale(DECIMAL_SCALE, ROUNDING_MODE);

        BigDecimal finalCredit = creditBeforeFee.subtract(exchangeFee).setScale(DECIMAL_SCALE, ROUNDING_MODE);

        return finalCredit;
    }

    @Transactional
    public List<ExchangeLogRecordDTO> getExchangeLog() {
        return exchangeRepository.findAll().stream()
                .map(this::convertToLogRecordDTO)
                .collect(Collectors.toList());
    }

    private ExchangeLogRecordDTO convertToLogRecordDTO(SalesExchange exchange) {
        ExchangeLogRecordDTO dto = new ExchangeLogRecordDTO();
        dto.setInvoiceNo(exchange.getOriginalInvoice().getInvoiceNo());
        dto.setCustomerName(exchange.getCustomer().getName());
        dto.setExchangeDate(exchange.getExchangeDate().toLocalDate());

        // Note: NewItemName/RetailPrice require a lookup from the item master table based on newItemId.
        // For this DTO conversion, we'll leave those fields empty or set a placeholder.
        dto.setReturnItemName(exchange.getReturnedItem().getItemName());
        dto.setCalculatedIntrinsicCredit(exchange.getCalculatedIntrinsicCredit());

        dto.setNewItemName("ID: " + exchange.getNewItemId());
        dto.setNewItemRetailPrice(BigDecimal.ZERO); // Placeholder: Actual price needs lookup

        dto.setCustomerFinalAdjustment(exchange.getCustomerFinalAdjustment());
        dto.setHandledBy(exchange.getHandledBy());
        dto.setGoldRateUsed(exchange.getGoldRateUsed());

        return dto;
    }
}