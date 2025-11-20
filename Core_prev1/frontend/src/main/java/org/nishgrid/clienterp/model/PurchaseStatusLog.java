package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class PurchaseStatusLog {
    private final Long id;
    private final PurchaseOrder purchaseOrder;
    private final String oldStatus;
    private final String newStatus;
    private final String changedBy;
    private final LocalDateTime changedAt;

    @JsonCreator
    public PurchaseStatusLog(@JsonProperty("id") Long id,
                             @JsonProperty("purchaseOrder") PurchaseOrder purchaseOrder,
                             @JsonProperty("oldStatus") String oldStatus,
                             @JsonProperty("newStatus") String newStatus,
                             @JsonProperty("changedBy") String changedBy,
                             @JsonProperty("changedAt") LocalDateTime changedAt) {
        this.id = id;
        this.purchaseOrder = purchaseOrder;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Long getId() { return id; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public String getOldStatus() { return oldStatus; }
    public String getNewStatus() { return newStatus; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
}