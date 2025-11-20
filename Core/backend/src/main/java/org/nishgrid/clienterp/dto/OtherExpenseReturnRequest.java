package org.nishgrid.clienterp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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
}