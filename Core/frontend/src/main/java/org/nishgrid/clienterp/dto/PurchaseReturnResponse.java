package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseReturnResponse {
    private Long id;
    private String returnNumber;
    private Long vendorId;
    private String vendorName;
    private Long purchaseInvoiceId;
    private String invoiceNumber;
    private LocalDate returnDate;
    private String reason;
    private BigDecimal amountReturned;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReturnNumber() { return returnNumber; }
    public void setReturnNumber(String returnNumber) { this.returnNumber = returnNumber; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public Long getPurchaseInvoiceId() { return purchaseInvoiceId; }
    public void setPurchaseInvoiceId(Long purchaseInvoiceId) { this.purchaseInvoiceId = purchaseInvoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public BigDecimal getAmountReturned() { return amountReturned; }
    public void setAmountReturned(BigDecimal amountReturned) { this.amountReturned = amountReturned; }
}