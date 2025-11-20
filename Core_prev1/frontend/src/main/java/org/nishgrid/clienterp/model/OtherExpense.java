package org.nishgrid.clienterp.model;

public class OtherExpense {
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