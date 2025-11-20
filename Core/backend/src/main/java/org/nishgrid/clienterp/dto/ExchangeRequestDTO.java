package org.nishgrid.clienterp.dto;

import java.time.LocalDate;

public class ExchangeRequestDTO {
    private Long originalInvoiceId;
    private Long returnItemId;
    private Long newItemId;
    private Double differenceAmount;
    private LocalDate exchangeDate;
    private Long customerId;
    private String remarks;
    private String handledBy;

    public Long getOriginalInvoiceId() {
        return originalInvoiceId;
    }

    public void setOriginalInvoiceId(Long originalInvoiceId) {
        this.originalInvoiceId = originalInvoiceId;
    }

    public Long getReturnItemId() {
        return returnItemId;
    }

    public void setReturnItemId(Long returnItemId) {
        this.returnItemId = returnItemId;
    }

    public Long getNewItemId() {
        return newItemId;
    }

    public void setNewItemId(Long newItemId) {
        this.newItemId = newItemId;
    }

    public Double getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(Double differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public LocalDate getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(LocalDate exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }
}