package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class PurchaseAuditLog {
    private final Long id;
    private final String userId;
    private final String actionType;
    private final String module;
    private final String details;
    private final LocalDateTime timestamp;

    @JsonCreator
    public PurchaseAuditLog(@JsonProperty("id") Long id,
                            @JsonProperty("userId") String userId,
                            @JsonProperty("actionType") String actionType,
                            @JsonProperty("module") String module,
                            @JsonProperty("details") String details,
                            @JsonProperty("timestamp") LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.actionType = actionType;
        this.module = module;
        this.details = details;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getActionType() { return actionType; }
    public String getModule() { return module; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}