package org.nishgrid.clienterp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hsn_summary")
public class HsnSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hsnCode;
    private Double totalQty;
    private Double taxableValue;
    private Double gstAmount;
    private String month; // "2025-08"

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}