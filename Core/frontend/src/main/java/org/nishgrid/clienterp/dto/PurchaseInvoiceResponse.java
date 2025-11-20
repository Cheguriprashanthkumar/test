package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseInvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long vendorId;
    private String vendorName;
    private Long purchaseOrderId;
    private String poNumber;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal gstAmount;
    private BigDecimal grandTotal;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getGstAmount() { return gstAmount; }
    public void setGstAmount(BigDecimal gstAmount) { this.gstAmount = gstAmount; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
}