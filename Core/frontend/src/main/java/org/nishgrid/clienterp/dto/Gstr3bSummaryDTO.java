package org.nishgrid.clienterp.dto;

import java.time.LocalDateTime;


public class Gstr3bSummaryDTO {
    private Long id;
    private String monthYear;
    private Double outwardTaxableSupplies;
    private Double inwardSupplies;
    private Double igst;
    private Double cgst;
    private Double sgst;
    private String filedStatus;
    private LocalDateTime filedDate;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
    public Double getOutwardTaxableSupplies() { return outwardTaxableSupplies; }
    public void setOutwardTaxableSupplies(Double outwardTaxableSupplies) { this.outwardTaxableSupplies = outwardTaxableSupplies; }
    public Double getInwardSupplies() { return inwardSupplies; }
    public void setInwardSupplies(Double inwardSupplies) { this.inwardSupplies = inwardSupplies; }
    public Double getIgst() { return igst; }
    public void setIgst(Double igst) { this.igst = igst; }
    public Double getCgst() { return cgst; }
    public void setCgst(Double cgst) { this.cgst = cgst; }
    public Double getSgst() { return sgst; }
    public void setSgst(Double sgst) { this.sgst = sgst; }
    public String getFiledStatus() { return filedStatus; }
    public void setFiledStatus(String filedStatus) { this.filedStatus = filedStatus; }
    public LocalDateTime getFiledDate() { return filedDate; }
    public void setFiledDate(LocalDateTime filedDate) { this.filedDate = filedDate; }
}