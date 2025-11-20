package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeLogRecordDTO {

    // --- Core Transaction Details ---
    private String invoiceNo;
    private LocalDate exchangeDate;
    private String customerName;
    private String handledBy;

    // --- Item Names ---
    private String returnItemName;
    private String newItemName;

    // --- Audit and Financial Fields (Replacing old 'differenceAmount') ---

    // The calculated credit value given for the returned item
    private BigDecimal calculatedIntrinsicCredit;

    // The full retail price of the new item
    private BigDecimal newItemRetailPrice;

    // The final net amount customer paid/received (the difference)
    private BigDecimal customerFinalAdjustment;

    // The gold rate used for auditing the transaction
    private BigDecimal goldRateUsed;

    // --- Getters and Setters ---

    public ExchangeLogRecordDTO() {
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getReturnItemName() {
        return returnItemName;
    }

    public void setReturnItemName(String returnItemName) {
        this.returnItemName = returnItemName;
    }

    public String getNewItemName() {
        return newItemName;
    }

    public void setNewItemName(String newItemName) {
        this.newItemName = newItemName;
    }

    public LocalDate getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(LocalDate exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }

    // New/Updated Getters and Setters

    public BigDecimal getCalculatedIntrinsicCredit() {
        return calculatedIntrinsicCredit;
    }

    public void setCalculatedIntrinsicCredit(BigDecimal calculatedIntrinsicCredit) {
        this.calculatedIntrinsicCredit = calculatedIntrinsicCredit;
    }

    public BigDecimal getNewItemRetailPrice() {
        return newItemRetailPrice;
    }

    public void setNewItemRetailPrice(BigDecimal newItemRetailPrice) {
        this.newItemRetailPrice = newItemRetailPrice;
    }

    public BigDecimal getCustomerFinalAdjustment() {
        return customerFinalAdjustment;
    }

    public void setCustomerFinalAdjustment(BigDecimal customerFinalAdjustment) {
        this.customerFinalAdjustment = customerFinalAdjustment;
    }

    public BigDecimal getGoldRateUsed() {
        return goldRateUsed;
    }

    public void setGoldRateUsed(BigDecimal goldRateUsed) {
        this.goldRateUsed = goldRateUsed;
    }
}