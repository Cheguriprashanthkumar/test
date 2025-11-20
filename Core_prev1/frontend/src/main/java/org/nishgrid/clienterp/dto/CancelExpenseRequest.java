package org.nishgrid.clienterp.dto;

public class CancelExpenseRequest {
    private String cancelledBy;
    private String cancelReason;


    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
}