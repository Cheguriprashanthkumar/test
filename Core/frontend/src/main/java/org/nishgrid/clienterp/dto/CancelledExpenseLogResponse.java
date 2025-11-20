package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CancelledExpenseLogResponse {
    private Long logId;
    private Long expenseId;
    private String originalExpenseDescription;
    private LocalDateTime cancelledOn;
    private String cancelledBy;
    private String cancelReason;
    private BigDecimal oldAmount;
    private String expenseCategory;


    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
    public String getOriginalExpenseDescription() { return originalExpenseDescription; }
    public void setOriginalExpenseDescription(String originalExpenseDescription) { this.originalExpenseDescription = originalExpenseDescription; }
    public LocalDateTime getCancelledOn() { return cancelledOn; }
    public void setCancelledOn(LocalDateTime cancelledOn) { this.cancelledOn = cancelledOn; }
    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public BigDecimal getOldAmount() { return oldAmount; }
    public void setOldAmount(BigDecimal oldAmount) { this.oldAmount = oldAmount; }
    public String getExpenseCategory() { return expenseCategory; }
    public void setExpenseCategory(String expenseCategory) { this.expenseCategory = expenseCategory; }
}