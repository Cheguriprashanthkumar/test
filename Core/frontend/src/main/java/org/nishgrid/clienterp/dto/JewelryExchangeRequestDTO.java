package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class JewelryExchangeRequestDTO {
    private Long originalInvoiceId;
    private Long returnItemId;
    private Long newItemId;
    private Long customerId;
    private LocalDate exchangeDate;
    private String remarks;
    private String handledBy;

    private BigDecimal goldRatePerGram24K;
    private BigDecimal platinumRatePerGramPure;
    private BigDecimal silverRatePerGramPure;

    private BigDecimal customerFinalAdjustmentAmount;

    // Getters and Setters (omitted for brevity)
    public Long getOriginalInvoiceId() { return originalInvoiceId; }
    public void setOriginalInvoiceId(Long originalInvoiceId) { this.originalInvoiceId = originalInvoiceId; }
    public Long getReturnItemId() { return returnItemId; }
    public void setReturnItemId(Long returnItemId) { this.returnItemId = returnItemId; }
    public Long getNewItemId() { return newItemId; }
    public void setNewItemId(Long newItemId) { this.newItemId = newItemId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public LocalDate getExchangeDate() { return exchangeDate; }
    public void setExchangeDate(LocalDate exchangeDate) { this.exchangeDate = exchangeDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getHandledBy() { return handledBy; }
    public void setHandledBy(String handledBy) { this.handledBy = handledBy; }
    public BigDecimal getGoldRatePerGram24K() { return goldRatePerGram24K; }
    public void setGoldRatePerGram24K(BigDecimal goldRatePerGram24K) { this.goldRatePerGram24K = goldRatePerGram24K; }
    public BigDecimal getPlatinumRatePerGramPure() { return platinumRatePerGramPure; }
    public void setPlatinumRatePerGramPure(BigDecimal platinumRatePerGramPure) { this.platinumRatePerGramPure = platinumRatePerGramPure; }
    public BigDecimal getSilverRatePerGramPure() { return silverRatePerGramPure; }
    public void setSilverRatePerGramPure(BigDecimal silverRatePerGramPure) { this.silverRatePerGramPure = silverRatePerGramPure; }
    public BigDecimal getCustomerFinalAdjustmentAmount() { return customerFinalAdjustmentAmount; }
    public void setCustomerFinalAdjustmentAmount(BigDecimal customerFinalAdjustmentAmount) { this.customerFinalAdjustmentAmount = customerFinalAdjustmentAmount; }
}