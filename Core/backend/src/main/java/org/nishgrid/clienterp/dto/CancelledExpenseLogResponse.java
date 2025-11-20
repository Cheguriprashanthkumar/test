package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.CancelledExpenseLog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CancelledExpenseLogResponse {
    private Long logId;
    private Long expenseId;
    private String originalExpenseDescription;
    private LocalDateTime cancelledOn;
    private String cancelledBy;
    private String cancelReason;
    private BigDecimal oldAmount;
    private String expenseCategory;

    public static CancelledExpenseLogResponse fromEntity(CancelledExpenseLog log) {
        CancelledExpenseLogResponse dto = new CancelledExpenseLogResponse();
        dto.setLogId(log.getLogId());
        dto.setExpenseId(log.getOtherExpense().getExpenseId());
        dto.setOriginalExpenseDescription(log.getOtherExpense().getExpenseDescription());
        dto.setCancelledOn(log.getCancelledOn());
        dto.setCancelledBy(log.getCancelledBy());
        dto.setCancelReason(log.getCancelReason());
        dto.setOldAmount(log.getOldAmount());
        dto.setExpenseCategory(log.getExpenseCategory());
        return dto;
    }
}