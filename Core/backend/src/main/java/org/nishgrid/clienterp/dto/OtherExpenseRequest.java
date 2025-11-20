package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.OtherExpense;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OtherExpenseRequest {
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
    private String createdBy;
    private String approvedBy;
    private OtherExpense.ApprovalStatus approvalStatus;
    private String branchLocation;
    private OtherExpense.ExpenseType expenseType;
    private LocalDate nextDueDate;
    private String remarks;
}