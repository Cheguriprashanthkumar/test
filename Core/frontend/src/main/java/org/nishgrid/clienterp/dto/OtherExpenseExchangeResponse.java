package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class OtherExpenseExchangeResponse {
    private Long exchangeId;
    private Long oldExpenseId;
    private String oldExpenseCategory;
    private Long newExpenseId;
    private String newExpenseCategory;
    private LocalDate exchangeDate;
    private Long vendorId;
    private String vendorName;
    private String reason;
    private BigDecimal adjustedAmount;
    private String approvedBy;
    private String createdBy;
    private String remarks;

    // Getters and Setters
    public Long getExchangeId() { return exchangeId; }
    public void setExchangeId(Long exchangeId) { this.exchangeId = exchangeId; }
    public Long getOldExpenseId() { return oldExpenseId; }
    public void setOldExpenseId(Long oldExpenseId) { this.oldExpenseId = oldExpenseId; }
    public String getOldExpenseCategory() { return oldExpenseCategory; }
    public void setOldExpenseCategory(String oldExpenseCategory) { this.oldExpenseCategory = oldExpenseCategory; }
    public Long getNewExpenseId() { return newExpenseId; }
    public void setNewExpenseId(Long newExpenseId) { this.newExpenseId = newExpenseId; }
    public String getNewExpenseCategory() { return newExpenseCategory; }
    public void setNewExpenseCategory(String newExpenseCategory) { this.newExpenseCategory = newExpenseCategory; }
    public LocalDate getExchangeDate() { return exchangeDate; }
    public void setExchangeDate(LocalDate exchangeDate) { this.exchangeDate = exchangeDate; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherExpenseExchangeResponse that = (OtherExpenseExchangeResponse) o;
        return Objects.equals(exchangeId, that.exchangeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchangeId);
    }
}