package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SaleUpdateDTO {
    private LocalDate invoiceDate;
    private String paymentMode;
    private String remarks;
    private String status;
    private BigDecimal paidAmount;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal gstPercent;
    private String customerState;
    private BigDecimal oldGoldValue;
    private List<SaleItemDTO> items;

    public BigDecimal getOldGoldValue() {
        return oldGoldValue;
    }

    public void setOldGoldValue(BigDecimal oldGoldValue) {
        this.oldGoldValue = oldGoldValue;
    }

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