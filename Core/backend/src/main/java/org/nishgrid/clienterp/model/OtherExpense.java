package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "other_expenses")
@Getter
@Setter
public class OtherExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    private LocalDate expenseDate;
    private String expenseCategory;
    @Lob private String expenseDescription;
    private BigDecimal amount;
    private BigDecimal gstPercent;
    private BigDecimal gstAmount;
    private Boolean isGstClaimable;
    private String paidTo;
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;
    private String referenceNumber;
    private String billNumber;
    private String billAttachment;
    private String createdBy;
    private String approvedBy;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    private String branchLocation;
    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;
    private LocalDate nextDueDate;
    @Lob private String remarks;

    public enum PaymentMode {
        CASH, UPI, BANK_TRANSFER, CHEQUE, CARD
    }
    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
    public enum ExpenseType {
        ONE_TIME, RECURRING
    }
}