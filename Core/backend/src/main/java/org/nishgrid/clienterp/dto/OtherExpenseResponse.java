package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.OtherExpense;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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

    public static OtherExpenseResponse fromEntity(OtherExpense expense) {
        OtherExpenseResponse dto = new OtherExpenseResponse();
        dto.setExpenseId(expense.getExpenseId());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setExpenseCategory(expense.getExpenseCategory());
        dto.setExpenseDescription(expense.getExpenseDescription());
        dto.setAmount(expense.getAmount());
        dto.setGstPercent(expense.getGstPercent());
        dto.setGstAmount(expense.getGstAmount());
        dto.setIsGstClaimable(expense.getIsGstClaimable());
        dto.setPaidTo(expense.getPaidTo());
        dto.setPaymentMode(expense.getPaymentMode());
        dto.setReferenceNumber(expense.getReferenceNumber());
        dto.setBillNumber(expense.getBillNumber());
        dto.setBillAttachment(expense.getBillAttachment());
        dto.setCreatedBy(expense.getCreatedBy());
        dto.setApprovedBy(expense.getApprovedBy());
        dto.setApprovalStatus(expense.getApprovalStatus());
        dto.setBranchLocation(expense.getBranchLocation());
        dto.setExpenseType(expense.getExpenseType());
        dto.setNextDueDate(expense.getNextDueDate());
        dto.setRemarks(expense.getRemarks());
        return dto;
    }
}