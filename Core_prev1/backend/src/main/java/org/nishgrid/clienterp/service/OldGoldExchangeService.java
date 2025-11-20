package org.nishgrid.clienterp.service;

import jakarta.validation.ValidationException;
import org.nishgrid.clienterp.dto.OldGoldExchangeRequestDTO;
import org.nishgrid.clienterp.dto.OldGoldExchangeItemDTO;
import org.nishgrid.clienterp.model.OldGoldExchange;
import org.nishgrid.clienterp.model.OldGoldExchangeItem;
import org.nishgrid.clienterp.repository.OldGoldExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OldGoldExchangeService {

    private final OldGoldExchangeRepository repository;
    private static final int DECIMAL_SCALE = 2;

    @Autowired
    public OldGoldExchangeService(OldGoldExchangeRepository repository) {
        this.repository = repository;
    }

    public String getNextAvailableBillNumber() {
        return repository.findFirstByOrderByPurchaseBillNoDesc()
                .map(lastBill -> {
                    String lastBillNo = lastBill.getPurchaseBillNo();
                    int lastNumber = Integer.parseInt(lastBillNo.substring(5));
                    return "PEXC-" + (lastNumber + 1);
                })
                .orElse("PEXC-1001");
    }

    @Transactional(rollbackFor = Exception.class)
    public OldGoldExchangeRequestDTO processExchange(OldGoldExchangeRequestDTO request) {
        validateRequest(request);

        BigDecimal overallPurchaseValue = BigDecimal.ZERO;
        for (OldGoldExchangeItemDTO item : request.getItems()) {
            calculateItemValues(item);
            overallPurchaseValue = overallPurchaseValue.add(item.getTotalItemValue());
        }

        request.setTotalPurchaseValue(round(overallPurchaseValue));
        calculateFinalPayout(request);

        String finalBillNo = getNextAvailableBillNumber();
        request.setPurchaseBillNo(finalBillNo);

        OldGoldExchange entity = mapToEntity(request);
        entity.setStatus("PENDING_PAYOUT");
        repository.save(entity);

        return request;
    }

    public List<OldGoldExchange> getPurchasesByDateRangeAndStatus(LocalDate startDate, LocalDate endDate, String status) {
        return repository.findAll().stream()
                .filter(e -> e.getPurchaseDate().isAfter(startDate.minusDays(1)) && e.getPurchaseDate().isBefore(endDate.plusDays(1)))
                .filter(e -> status == null || status.isEmpty() || status.equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
    }

    public Optional<OldGoldExchange> getPurchaseDetailByBillNo(String billNo) {
        return repository.findAll().stream()
                .filter(e -> e.getPurchaseBillNo().equalsIgnoreCase(billNo))
                .findFirst();
    }

    @Transactional(rollbackFor = Exception.class)
    public OldGoldExchange updatePaymentStatus(String billNo, BigDecimal amountPaid) {
        Optional<OldGoldExchange> optionalEntity = getPurchaseDetailByBillNo(billNo);

        if (optionalEntity.isEmpty()) {
            throw new ValidationException("Purchase Bill not found.");
        }

        OldGoldExchange entity = optionalEntity.get();
        if (entity.getStatus().equals("PAID")) {
            throw new ValidationException("This bill is already fully paid.");
        }

        BigDecimal outstandingAmount = entity.getNetPayableAmount();
        BigDecimal roundedOutstanding = round(outstandingAmount);
        BigDecimal roundedPaidAmount = round(amountPaid);

        if (roundedPaidAmount.compareTo(roundedOutstanding) > 0) {
            throw new ValidationException("Amount paid exceeds net payable amount.");
        }

        if (roundedPaidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be positive.");
        }

        if (roundedPaidAmount.compareTo(roundedOutstanding) == 0) {
            entity.setStatus("PAID");
        } else {
            entity.setStatus("PARTIALLY_PAID");
        }

        return repository.save(entity);
    }

    private void calculateItemValues(OldGoldExchangeItemDTO item) {
        BigDecimal grossWeight = parse(item.getGrossWeight()).setScale(3, RoundingMode.HALF_UP);
        BigDecimal rate = parse(item.getRatePerGram()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal wastagePercent = parse(item.getWastagePercent()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal diamondCarat = parse(item.getDiamondCarat()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal diamondRate = parse(item.getDiamondRate()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal deductionCharge = parse(item.getDeductionCharge()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        BigDecimal netWeightDecimal = grossWeight.subtract(grossWeight.multiply(wastagePercent).divide(new BigDecimal("100"), 3, RoundingMode.HALF_UP));
        item.setNetWeight(netWeightDecimal.doubleValue());

        BigDecimal metalValue = netWeightDecimal.multiply(rate).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal diamondValue = diamondCarat.multiply(diamondRate).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        BigDecimal totalValue = metalValue.add(diamondValue).subtract(deductionCharge).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        if (totalValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Item '" + item.getItemName() + "' results in a negative value after deductions.");
        }

        item.setTotalItemValue(round(totalValue));
    }

    private void calculateFinalPayout(OldGoldExchangeRequestDTO request) {
        BigDecimal totalValue = request.getTotalPurchaseValue();
        BigDecimal feePercent = parse(request.getProcessingFeePercent()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        BigDecimal feeAmount = totalValue.multiply(feePercent).divide(new BigDecimal("100"), DECIMAL_SCALE, RoundingMode.HALF_UP);
        request.setProcessingFeeAmount(round(feeAmount));

        BigDecimal netPayable = totalValue.subtract(feeAmount);
        request.setNetPayableAmount(round(netPayable));
    }

    private OldGoldExchange mapToEntity(OldGoldExchangeRequestDTO dto) {
        OldGoldExchange entity = new OldGoldExchange();
        entity.setPurchaseBillNo(dto.getPurchaseBillNo());
        entity.setSellerName(dto.getSellerName());
        entity.setSellerMobile(dto.getSellerMobile());
        entity.setSellerEmail(dto.getSellerEmail());
        entity.setSellerAddress(dto.getSellerAddress());
        entity.setSellerGstin(dto.getSellerGstin());
        entity.setPurchaseDate(dto.getPurchaseDate());
        entity.setPayoutMode(dto.getPayoutMode());
        entity.setOtherPayoutMode(dto.getOtherPayoutMode());
        entity.setRemarks(dto.getRemarks());
        entity.setTotalPurchaseValue(dto.getTotalPurchaseValue());
        entity.setProcessingFeePercent(dto.getProcessingFeePercent());
        entity.setProcessingFeeAmount(dto.getProcessingFeeAmount());
        entity.setNetPayableAmount(dto.getNetPayableAmount());

        List<OldGoldExchangeItem> itemEntities = dto.getItems().stream()
                .map(itemDto -> {
                    OldGoldExchangeItem itemEntity = mapItemDtoToEntity(itemDto);
                    itemEntity.setExchange(entity);
                    return itemEntity;
                })
                .collect(Collectors.toList());

        entity.setItems(itemEntities);
        return entity;
    }

    private OldGoldExchangeItem mapItemDtoToEntity(OldGoldExchangeItemDTO dto) {
        OldGoldExchangeItem entity = new OldGoldExchangeItem();
        entity.setItemName(dto.getItemName());
        entity.setMetalType(dto.getMetalType());
        entity.setPurity(dto.getPurity());
        entity.setGrossWeight(dto.getGrossWeight());
        entity.setNetWeight(dto.getNetWeight());
        entity.setWastagePercent(dto.getWastagePercent());
        entity.setRatePerGram(dto.getRatePerGram());
        entity.setDiamondCarat(dto.getDiamondCarat());
        entity.setDiamondRate(dto.getDiamondRate());
        entity.setDeductionCharge(dto.getDeductionCharge());
        entity.setTotalItemValue(dto.getTotalItemValue());
        return entity;
    }

    private void validateRequest(OldGoldExchangeRequestDTO request) {
        if (request.getSellerName() == null || request.getSellerName().trim().isEmpty()) {
            throw new ValidationException("Seller name is required.");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ValidationException("At least one item must be added to the purchase bill.");
        }
        for (OldGoldExchangeItemDTO item : request.getItems()) {
            if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
                throw new ValidationException("All items must have a name.");
            }
        }
    }

    private BigDecimal parse(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof String) {
            try { return new BigDecimal((String) value); } catch (NumberFormatException e) { return BigDecimal.ZERO; }
        }
        if (value instanceof Double) return BigDecimal.valueOf((Double) value);
        return (BigDecimal) value;
    }

    private BigDecimal round(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        BigDecimal fractional = value.remainder(BigDecimal.ONE);
        BigDecimal integerPart = value.setScale(0, RoundingMode.DOWN);
        if (fractional.compareTo(new BigDecimal("0.50")) < 0) {
            return integerPart;
        } else {
            return integerPart.add(BigDecimal.ONE);
        }
    }
}