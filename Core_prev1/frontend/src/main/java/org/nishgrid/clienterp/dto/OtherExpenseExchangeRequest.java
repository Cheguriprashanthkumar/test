package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OtherExpenseExchangeRequest {
    private Long oldExpenseId;
    private Long newExpenseId;
    private LocalDate exchangeDate;
    private Long vendorId;
    private String reason;
    private BigDecimal adjustedAmount;
    private String approvedBy;
    private String createdBy;
    private String remarks;

    // Getters and Setters
    public Long getOldExpenseId() { return oldExpenseId; }
    public void setOldExpenseId(Long oldExpenseId) { this.oldExpenseId = oldExpenseId; }
    public Long getNewExpenseId() { return newExpenseId; }
    public void setNewExpenseId(Long newExpenseId) { this.newExpenseId = newExpenseId; }
    public LocalDate getExchangeDate() { return exchangeDate; }
    public void setExchangeDate(LocalDate exchangeDate) { this.exchangeDate = exchangeDate; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public BigDecimal getAdjustedAmount() { return adjustedAmount; }
    public void setAdjustedAmount(BigDecimal adjustedAmount) { this.adjustedAmount = adjustedAmount; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}