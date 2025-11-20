package org.nishgrid.clienterp.dto;

public class HsnAggregationDTO {
    private String hsnCode;
    private Double totalQty;
    private Double taxableValue;
    private Double gstAmount;

    public HsnAggregationDTO(String hsnCode, Double totalQty, Double taxableValue, Double gstAmount) {
        this.hsnCode = hsnCode;
        this.totalQty = totalQty != null ? totalQty : 0.0;
        this.taxableValue = taxableValue != null ? taxableValue : 0.0;
        this.gstAmount = gstAmount != null ? gstAmount : 0.0;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public Double getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Double totalQty) {
        this.totalQty = totalQty;
    }

    public Double getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(Double taxableValue) {
        this.taxableValue = taxableValue;
    }

    public Double getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(Double gstAmount) {
        this.gstAmount = gstAmount;
    }
}