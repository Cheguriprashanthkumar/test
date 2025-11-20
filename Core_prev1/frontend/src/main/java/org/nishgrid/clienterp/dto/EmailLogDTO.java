package org.nishgrid.clienterp.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class EmailLogDTO {

    private Long emailId;
    private Long invoiceId;
    private Long customerId;
    private String emailAddress;
    private String status;
    private LocalDateTime sentTime;
    private String errorMessage;

    public EmailLogDTO() {
    }

    public EmailLogDTO(Long emailId, Long invoiceId, Long customerId, String emailAddress, String status, LocalDateTime sentTime, String errorMessage) {
        this.emailId = emailId;
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.emailAddress = emailAddress;
        this.status = status;
        this.sentTime = sentTime;
        this.errorMessage = errorMessage;
    }

    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailLogDTO that = (EmailLogDTO) o;
        return Objects.equals(emailId, that.emailId) && Objects.equals(invoiceId, that.invoiceId) && Objects.equals(customerId, that.customerId) && Objects.equals(emailAddress, that.emailAddress) && Objects.equals(status, that.status) && Objects.equals(sentTime, that.sentTime) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailId, invoiceId, customerId, emailAddress, status, sentTime, errorMessage);
    }

    @Override
    public String toString() {
        return "EmailLogDTO{" +
                "emailId=" + emailId +
                ", invoiceId=" + invoiceId +
                ", customerId=" + customerId +
                ", emailAddress='" + emailAddress + '\'' +
                ", status='" + status + '\'' +
                ", sentTime=" + sentTime +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
