package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class SalesAuditLog {
    private final IntegerProperty logId;
    private final IntegerProperty invoiceId;
    private final StringProperty action;
    private final StringProperty performedBy;
    private final StringProperty timestamp;
    private final StringProperty oldData;
    private final StringProperty newData;

    public SalesAuditLog(int logId, int invoiceId, String action, String performedBy, String timestamp, String oldData, String newData) {
        this.logId = new SimpleIntegerProperty(logId);
        this.invoiceId = new SimpleIntegerProperty(invoiceId);
        this.action = new SimpleStringProperty(action);
        this.performedBy = new SimpleStringProperty(performedBy);
        this.timestamp = new SimpleStringProperty(timestamp);
        this.oldData = new SimpleStringProperty(oldData);
        this.newData = new SimpleStringProperty(newData);
    }

    public IntegerProperty logIdProperty() { return logId; }
    public IntegerProperty invoiceIdProperty() { return invoiceId; }
    public StringProperty actionProperty() { return action; }
    public StringProperty performedByProperty() { return performedBy; }
    public StringProperty timestampProperty() { return timestamp; }
    public StringProperty oldDataProperty() { return oldData; }
    public StringProperty newDataProperty() { return newData; }
}
