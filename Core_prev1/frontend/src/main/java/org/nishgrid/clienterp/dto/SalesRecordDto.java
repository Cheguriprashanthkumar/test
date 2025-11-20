package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesRecordDto {
    private Long id;
    private String invoiceNo;
    private LocalDate invoiceDate;
    private String customerName;
    private BigDecimal netAmount;
    private BigDecimal finalAmount;
    private BigDecimal oldGoldValue;
    private String salesType;
    private String paymentMode;
    private BigDecimal totalAmount;
    private String status;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    public SalesRecordDto setOldGoldValue(BigDecimal oldGoldValue) {
        this.oldGoldValue = oldGoldValue;
        return this;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }
}