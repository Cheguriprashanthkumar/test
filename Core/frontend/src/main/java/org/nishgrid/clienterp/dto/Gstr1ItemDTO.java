package org.nishgrid.clienterp.dto;

import java.time.LocalDate;

public class Gstr1ItemDTO {
    private Long id;
    private Long invoiceId;
    private String customerGstin;
    private LocalDate invoiceDate;
    private String itemHsn;
    private Double taxableValue;
    private Double gstRate;
    private Double cgstAmount;
    private Double sgstAmount;
    private Double igstAmount;
    private String exportMonth;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerGstin() {
        return customerGstin;
    }

    public void setCustomerGstin(String customerGstin) {
        this.customerGstin = customerGstin;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getItemHsn() {
        return itemHsn;
    }

    public void setItemHsn(String itemHsn) {
        this.itemHsn = itemHsn;
    }

    public Double getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(Double taxableValue) {
        this.taxableValue = taxableValue;
    }

    public Double getGstRate() {
        return gstRate;
    }

    public void setGstRate(Double gstRate) {
        this.gstRate = gstRate;
    }

    public Double getCgstAmount() {
        return cgstAmount;
    }

    public void setCgstAmount(Double cgstAmount) {
        this.cgstAmount = cgstAmount;
    }

    public Double getSgstAmount() {
        return sgstAmount;
    }

    public void setSgstAmount(Double sgstAmount) {
        this.sgstAmount = sgstAmount;
    }

    public Double getIgstAmount() {
        return igstAmount;
    }

    public void setIgstAmount(Double igstAmount) {
        this.igstAmount = igstAmount;
    }

    public String getExportMonth() {
        return exportMonth;
    }

    public void setExportMonth(String exportMonth) {
        this.exportMonth = exportMonth;
    }
}