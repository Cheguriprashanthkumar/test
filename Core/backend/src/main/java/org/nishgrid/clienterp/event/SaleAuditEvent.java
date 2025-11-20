package org.nishgrid.clienterp.event;

import org.nishgrid.clienterp.model.SalesInvoice;
import java.util.Map;

public class SaleAuditEvent {
    private final String action;
    private final Map<String, Object> oldState;
    private final SalesInvoice newState;

    public SaleAuditEvent(String action, Map<String, Object> oldState, SalesInvoice newState) {
        this.action = action;
        this.oldState = oldState;
        this.newState = newState;
    }

    public String getAction() {
        return action;
    }

    public Map<String, Object> getOldState() {
        return oldState;
    }

    public SalesInvoice getNewState() {
        return newState;
    }
}