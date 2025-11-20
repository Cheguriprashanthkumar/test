package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class EmailLog {
    private final IntegerProperty emailId;
    private final IntegerProperty invoiceId;
    private final IntegerProperty customerId;
    private final StringProperty emailAddress;
    private final StringProperty status;
    private final StringProperty sentTime;
    private final StringProperty errorMessage;

    public EmailLog(int emailId, int invoiceId, int customerId, String emailAddress, String status, String sentTime, String errorMessage) {
        this.emailId = new SimpleIntegerProperty(emailId);
        this.invoiceId = new SimpleIntegerProperty(invoiceId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.status = new SimpleStringProperty(status);
        this.sentTime = new SimpleStringProperty(sentTime);
        this.errorMessage = new SimpleStringProperty(errorMessage);
    }

    public IntegerProperty emailIdProperty() {
        return emailId;
    }

    public IntegerProperty invoiceIdProperty() {
        return invoiceId;
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public StringProperty emailAddressProperty() {
        return emailAddress;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty sentTimeProperty() {
        return sentTime;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }
}
