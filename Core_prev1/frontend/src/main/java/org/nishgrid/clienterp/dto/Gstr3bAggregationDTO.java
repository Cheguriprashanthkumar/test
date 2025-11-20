package org.nishgrid.clienterp.dto;

public class Gstr3bAggregationDTO {
    private Double outwardTaxableSupplies;
    private Double cgst;
    private Double sgst;
    private Double igst;

    public Gstr3bAggregationDTO(Double outwardTaxableSupplies, Double cgst, Double sgst, Double igst) {
        this.outwardTaxableSupplies = outwardTaxableSupplies;
        this.cgst = cgst;
        this.sgst = sgst;
        this.igst = igst;
    }

    public Double getOutwardTaxableSupplies() {
        return outwardTaxableSupplies;
    }

    public void setOutwardTaxableSupplies(Double outwardTaxableSupplies) {
        this.outwardTaxableSupplies = outwardTaxableSupplies;
    }

    public Double getCgst() {
        return cgst;
    }

    public void setCgst(Double cgst) {
        this.cgst = cgst;
    }

    public Double getSgst() {
        return sgst;
    }

    public void setSgst(Double sgst) {
        this.sgst = sgst;
    }

    public Double getIgst() {
        return igst;
    }

    public void setIgst(Double igst) {
        this.igst = igst;
    }
}