package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.OtherExpense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class OtherExpenseResponse {
    private Long expenseId;
    private LocalDate expenseDate;
    private String expenseCategory;
    private String expenseDescription;
    private BigDecimal amount;
    private BigDecimal gstPercent;
    private BigDecimal gstAmount;
    private Boolean isGstClaimable;
    private String paidTo;
    private OtherExpense.PaymentMode paymentMode;
    private String referenceNumber;
    private String billNumber;
    private String billAttachment;
    private String createdBy;
    private String approvedBy;
    private OtherExpense.ApprovalStatus approvalStatus;
    private String branchLocation;
    private OtherExpense.ExpenseType expenseType;
    private LocalDate nextDueDate;
    private String remarks;

    // Getters and Setters
    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
    public String getBillAttachment() { return billAttachment; }
    public void setBillAttachment(String billAttachment) { this.billAttachment = billAttachment; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public String getExpenseCategory() { return expenseCategory; }
    public void setExpenseCategory(String expenseCategory) { this.expenseCategory = expenseCategory; }
    public String getExpenseDescription() { return expenseDescription; }
    public void setExpenseDescription(String expenseDescription) { this.expenseDescription = expenseDescription; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getGstPercent() { return gstPercent; }
    public void setGstPercent(BigDecimal gstPercent) { this.gstPercent = gstPercent; }
    public BigDecimal getGstAmount() { return gstAmount; }
    public void setGstAmount(BigDecimal gstAmount) { this.gstAmount = gstAmount; }
    public Boolean getIsGstClaimable() { return isGstClaimable; }
    public void setIsGstClaimable(Boolean isGstClaimable) { this.isGstClaimable = isGstClaimable; }
    public String getPaidTo() { return paidTo; }
    public void setPaidTo(String paidTo) { this.paidTo = paidTo; }
    public OtherExpense.PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(OtherExpense.PaymentMode paymentMode) { this.paymentMode = paymentMode; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public OtherExpense.ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(OtherExpense.ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getBranchLocation() { return branchLocation; }
    public void setBranchLocation(String branchLocation) { this.branchLocation = branchLocation; }
    public OtherExpense.ExpenseType getExpenseType() { return expenseType; }
    public void setExpenseType(OtherExpense.ExpenseType expenseType) { this.expenseType = expenseType; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherExpenseResponse that = (OtherExpenseResponse) o;
        return Objects.equals(expenseId, that.expenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseId);
    }
}