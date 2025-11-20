package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OtherExpenseReturnRequest {
    private Long expenseId;
    private LocalDate returnDate;
    private BigDecimal returnedAmount;
    private String refundMode;
    private String refundReferenceNo;
    private String returnedBy;
    private String returnReason;
    private String receivedBy;
    private String remarks;
    private String approvedBy;

    // Getters and Setters
    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
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
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}