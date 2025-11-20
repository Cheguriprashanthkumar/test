package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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
}