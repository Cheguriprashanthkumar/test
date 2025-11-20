package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseReturnRequest {
    private String returnNumber;
    private Long vendorId;
    private Long purchaseInvoiceId;
    private LocalDate returnDate;
    private String reason;
    private BigDecimal amountReturned;

    // Getters and Setters
    public String getReturnNumber() { return returnNumber; }
    public void setReturnNumber(String returnNumber) { this.returnNumber = returnNumber; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public Long getPurchaseInvoiceId() { return purchaseInvoiceId; }
    public void setPurchaseInvoiceId(Long purchaseInvoiceId) { this.purchaseInvoiceId = purchaseInvoiceId; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public BigDecimal getAmountReturned() { return amountReturned; }
    public void setAmountReturned(BigDecimal amountReturned) { this.amountReturned = amountReturned; }
}