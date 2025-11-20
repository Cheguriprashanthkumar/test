package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class OtherExpenseReturnResponse {
    private Long returnId;
    private Long expenseId;
    private String expenseCategory;
    private LocalDate returnDate;
    private BigDecimal returnedAmount;
    private String refundMode;
    private String refundReferenceNo;
    private String returnedBy;
    private String returnReason;
    private String receivedBy;
    private String remarks;
    private String attachmentPath;
    private LocalDateTime createdOn;
    private String approvedBy;

    // Getters and Setters
    public Long getReturnId() { return returnId; }
    public void setReturnId(Long returnId) { this.returnId = returnId; }
    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
    public String getExpenseCategory() { return expenseCategory; }
    public void setExpenseCategory(String expenseCategory) { this.expenseCategory = expenseCategory; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public BigDecimal getReturnedAmount() { return returnedAmount; }
    public void setReturnedAmount(BigDecimal returnedAmount) { this.returnedAmount = returnedAmount; }
    public String getRefundMode() { return refundMode; }
    public void setRefundMode(String refundMode) { this.refundMode = refundMode; }
    public String getRefundReferenceNo() { return refundReferenceNo; }
    public void setRefundReferenceNo(String refundReferenceNo) { this.refundReferenceNo = refundReferenceNo; }
    public String getReturnedBy() { return returnedBy; }
    public void setReturnedBy(String returnedBy) { this.returnedBy = returnedBy; }
    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }
    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherExpenseReturnResponse that = (OtherExpenseReturnResponse) o;
        return Objects.equals(returnId, that.returnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId);
    }
}