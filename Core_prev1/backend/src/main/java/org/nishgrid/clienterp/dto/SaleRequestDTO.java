package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SaleRequestDTO {
    private String customerName;
    private String customerMobile;
    private String customerEmail;
    private String customerAddress;
    private String customerState;
    private String customerGstin;

    private LocalDate invoiceDate;
    private String paymentMode;
    private String otherPaymentMode;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal gstPercent;
    private String remarks;
    private BigDecimal oldGoldValue;

    private List<SaleItemDTO> items;

    @Data
    public static class SaleItemDTO {
        private String itemName;
        private String hsnCode;
        private String purity;
        private double grossWeight;
        private double netWeight;
        private BigDecimal ratePerGram;
        private BigDecimal makingCharge;
        private BigDecimal makingChargeAmount;
        private BigDecimal diamondCarat;
        private BigDecimal diamondRate;
    }
}