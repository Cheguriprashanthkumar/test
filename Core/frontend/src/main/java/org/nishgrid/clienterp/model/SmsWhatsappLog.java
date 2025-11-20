package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class SmsWhatsappLog {
    private final IntegerProperty messageId;
    private final IntegerProperty customerId;
    private final StringProperty messageType;
    private final StringProperty status;
    private final StringProperty timestamp;

    public SmsWhatsappLog(int messageId, int customerId, String messageType, String status, String timestamp) {
        this.messageId = new SimpleIntegerProperty(messageId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.messageType = new SimpleStringProperty(messageType);
        this.status = new SimpleStringProperty(status);
        this.timestamp = new SimpleStringProperty(timestamp);
    }

    public int getMessageId() { return messageId.get(); }
    public IntegerProperty messageIdProperty() { return messageId; }

    public int getCustomerId() { return customerId.get(); }
    public IntegerProperty customerIdProperty() { return customerId; }

    public String getMessageType() { return messageType.get(); }
    public StringProperty messageTypeProperty() { return messageType; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }

    public String getTimestamp() { return timestamp.get(); }
    public StringProperty timestampProperty() { return timestamp; }
}
