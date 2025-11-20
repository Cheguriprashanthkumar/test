package org.nishgrid.clienterp.service;

import jakarta.persistence.EntityNotFoundException;
import org.nishgrid.clienterp.dto.ReturnRecordDTO;
import org.nishgrid.clienterp.dto.ReturnRequestDTO;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.model.SalesItem;
import org.nishgrid.clienterp.model.SalesReturn;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.nishgrid.clienterp.repository.SalesItemRepository;
import org.nishgrid.clienterp.repository.SalesReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReturnService {

    @Autowired
    private SalesReturnRepository salesReturnRepository;
    @Autowired
    private SalesInvoiceRepository salesInvoiceRepository;
    @Autowired
    private SalesItemRepository salesItemRepository;

    private static final int STANDARD_SCALE = 2;
    private static final RoundingMode STANDARD_ROUNDING = RoundingMode.HALF_UP;

    @Transactional
    public SalesReturn processReturn(ReturnRequestDTO dto) {
        SalesInvoice invoice = salesInvoiceRepository.findById(dto.getInvoiceId())
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + dto.getInvoiceId()));

        SalesItem item = salesItemRepository.findById(dto.getSalesItemId())
                .orElseThrow(() -> new EntityNotFoundException("Sales Item not found with ID: " + dto.getSalesItemId()));

        if (!"Paid".equalsIgnoreCase(invoice.getStatus()) && !"Partially Returned".equalsIgnoreCase(invoice.getStatus())) {
            throw new IllegalStateException("Returns are only allowed for fully paid or partially returned invoices.");
        }

        if (!item.getInvoice().getInvoiceId().equals(invoice.getInvoiceId())) {
            throw new IllegalArgumentException("Item does not belong to the specified invoice.");
        }

        BigDecimal totalReturnedQuantity = salesReturnRepository.findTotalReturnedQuantityBySalesItemId(item.getSalesItemId())
                .orElse(BigDecimal.ZERO);
        BigDecimal requestedQuantity = BigDecimal.valueOf(dto.getQuantity());
        // Use net weight for quantity check, assuming it's the item's unit of sale quantity
        BigDecimal originalQuantity = Optional.ofNullable(item.getNetWeight()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);

        if (originalQuantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Original item quantity (Net Weight) is zero, cannot process return.");
        }

        BigDecimal remainingQuantity = originalQuantity.subtract(totalReturnedQuantity);

        if (requestedQuantity.compareTo(BigDecimal.ZERO) <= 0 || requestedQuantity.compareTo(remainingQuantity) > 0) {
            throw new IllegalArgumentException("Invalid return quantity. Cannot return more than what was sold or already returned.");
        }

        // --- FIXED: Calculate the per-item refundable base value ---
        // Refundable amount is ONLY the proportional value of the gold and diamonds,
        // thereby *excluding* Making Charge, GST, and Discount from the refund.
        BigDecimal goldValue = Optional.ofNullable(item.getNetWeight()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO).multiply(item.getRatePerGram());
        BigDecimal diamondValue = Optional.ofNullable(item.getDiamondCarat()).orElse(BigDecimal.ZERO).multiply(item.getDiamondRate());
        BigDecimal itemBasePrice = goldValue.add(diamondValue);

        // This is the total value *before* making charges, GST, and discount.
        // We calculate the proportional amount of this base value to refund.
        BigDecimal calculatedReturnAmount = itemBasePrice
                .divide(originalQuantity, STANDARD_SCALE, STANDARD_ROUNDING) // Value per unit
                .multiply(requestedQuantity); // Total value to refund for the quantity

        // This ensures that only the base commodity value is refunded.
        // The business keeps the profit/cost portions (Making Charge, GST, Discount).
        // --- END FIX ---

        SalesReturn salesReturn = new SalesReturn();
        salesReturn.setSalesInvoice(invoice);
        salesReturn.setSalesItem(item);
        salesReturn.setReturnDate(dto.getReturnDate());
        salesReturn.setReturnReason(dto.getReturnReason());
        salesReturn.setQuantity(requestedQuantity.intValue());
        salesReturn.setReturnAmount(calculatedReturnAmount.setScale(STANDARD_SCALE, STANDARD_ROUNDING));
        salesReturn.setRefundMode(dto.getRefundMode());
        salesReturn.setHandledBy(dto.getHandledBy());

        SalesReturn savedReturn = salesReturnRepository.save(salesReturn);

        // Update invoice totals and status
        updateInvoiceStatusAndAmountsAfterReturn(invoice);

        return savedReturn;
    }

    private void updateInvoiceStatusAndAmountsAfterReturn(SalesInvoice invoice) {
        boolean anyItemPartiallyReturned = false;

        // Get total return amount for invoice (this is the value actually refunded/credited)
        BigDecimal totalReturnAmountForInvoice = salesReturnRepository.findTotalReturnedAmountByInvoiceId(invoice.getInvoiceId()).orElse(BigDecimal.ZERO);

        // The only thing we must update here is the status.
        // The new due amount is recalculated in SaleService.recalcDueAmount when accessed.

        // Check if any item is partially returned
        for (SalesItem item : invoice.getSalesItems()) {
            BigDecimal totalReturnedQuantity = salesReturnRepository.findTotalReturnedQuantityBySalesItemId(item.getSalesItemId())
                    .orElse(BigDecimal.ZERO);
            if (totalReturnedQuantity.compareTo(BigDecimal.ZERO) > 0) {
                anyItemPartiallyReturned = true;
            }
            // Check if item is fully returned (quantity matches original net weight)
            BigDecimal originalQuantity = Optional.ofNullable(item.getNetWeight()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
            if (originalQuantity.compareTo(BigDecimal.ZERO) > 0 && totalReturnedQuantity.compareTo(originalQuantity) >= 0) {
                // Item is fully returned.
            } else if (totalReturnedQuantity.compareTo(BigDecimal.ZERO) > 0) {
                // Item is partially returned
                anyItemPartiallyReturned = true;
            }
        }

        // Check if *all* items are fully returned (optional, but good practice)
        boolean allItemsFullyReturned = invoice.getSalesItems().stream()
                .allMatch(item -> {
                    BigDecimal totalReturned = salesReturnRepository.findTotalReturnedQuantityBySalesItemId(item.getSalesItemId()).orElse(BigDecimal.ZERO);
                    BigDecimal original = Optional.ofNullable(item.getNetWeight()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
                    return original.compareTo(BigDecimal.ZERO) == 0 || totalReturned.compareTo(original) >= 0;
                });


        // Update invoice status based on due amount (recalcDueAmount must be called first if we rely on it here)
        // For simplicity, we stick to the provided status logic but ensure the due amount is zero before setting to "Paid".

        // Call recalcDueAmount here to ensure we have the correct due amount for status check
        // NOTE: This logic is moved to SaleService for a single source of truth (recalcDueAmount).

        // Simplified status update:
        // PENDING/PARTIALLY PAID status comes from SaleService.recalcDueAmount logic.
        // We only set "Partially Returned" or "Returned" (if all items are returned and due is settled)

        // For now, only set Partially Returned to allow SaleService to handle PAID/PARTIALLY PAID based on due amount.
        if (anyItemPartiallyReturned) {
            invoice.setStatus("Partially Returned");
        } else if (invoice.getDueAmount().compareTo(BigDecimal.ZERO) == 0) {
            // If nothing is partially returned, let SaleService set PAID/PARTIALLY PAID/PENDING
        }


        // IMPORTANT: We need to ensure the invoice totals are updated before saving if we rely on them.
        // Since SaleService.recalcDueAmount does this correctly by reading from SalesReturnRepository,
        // we just save the status change.

        salesInvoiceRepository.save(invoice);
    }

    public List<ReturnRecordDTO> findAllReturns() {
        return salesReturnRepository.findAll().stream()
                .map(this::convertToReturnRecordDTO)
                .collect(Collectors.toList());
    }

    private ReturnRecordDTO convertToReturnRecordDTO(SalesReturn salesReturn) {
        ReturnRecordDTO dto = new ReturnRecordDTO();
        dto.setInvoiceNo(salesReturn.getSalesInvoice().getInvoiceNo());
        dto.setItemName(salesReturn.getSalesItem().getItemName());
        dto.setReturnDate(salesReturn.getReturnDate());
        dto.setReturnAmount(salesReturn.getReturnAmount());
        dto.setQuantity(salesReturn.getQuantity());
        dto.setReturnReason(salesReturn.getReturnReason());
        dto.setHandledBy(salesReturn.getHandledBy());
        return dto;
    }

    public BigDecimal findTotalReturnedAmountByDateRange(LocalDate startDate, LocalDate endDate) {
        return salesReturnRepository.findTotalReturnedAmountByDateRange(startDate, endDate).orElse(BigDecimal.ZERO);
    }

    public List<SalesReturn> processReturns(List<ReturnRequestDTO> returnRequestDTOs) {
        List<SalesReturn> createdReturns = new ArrayList<>();
        for (ReturnRequestDTO dto : returnRequestDTOs) {
            SalesReturn newReturn = processReturn(dto);
            createdReturns.add(newReturn);
        }
        return createdReturns;
    }
}