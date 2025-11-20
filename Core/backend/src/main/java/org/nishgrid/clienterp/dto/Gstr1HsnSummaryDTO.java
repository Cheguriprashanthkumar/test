package org.nishgrid.clienterp.dto;

public class Gstr1HsnSummaryDTO {
    private String itemHsn;
    private Double totalTaxableValue;
    private Double totalIgstAmount;
    private Double totalCgstAmount;
    private Double totalSgstAmount;

    // This constructor matches the fields in your repository query
    public Gstr1HsnSummaryDTO(String itemHsn, Double totalTaxableValue, Double totalIgstAmount, Double totalCgstAmount, Double totalSgstAmount) {
        this.itemHsn = itemHsn;
        this.totalTaxableValue = totalTaxableValue;
        this.totalIgstAmount = totalIgstAmount;
        this.totalCgstAmount = totalCgstAmount;
        this.totalSgstAmount = totalSgstAmount;
    }

    // Getters and Setters
    public String getItemHsn() {
        return itemHsn;
    }

    public void setItemHsn(String itemHsn) {
        this.itemHsn = itemHsn;
    }

    public Double getTotalTaxableValue() {
        return totalTaxableValue;
    }

    public void setTotalTaxableValue(Double totalTaxableValue) {
        this.totalTaxableValue = totalTaxableValue;
    }

    public Double getTotalIgstAmount() {
        return totalIgstAmount;
    }

    public void setTotalIgstAmount(Double totalIgstAmount) {
        this.totalIgstAmount = totalIgstAmount;
    }

    public Double getTotalCgstAmount() {
        return totalCgstAmount;
    }

    public void setTotalCgstAmount(Double totalCgstAmount) {
        this.totalCgstAmount = totalCgstAmount;
    }

    public Double getTotalSgstAmount() {
        return totalSgstAmount;
    }

    public void setTotalSgstAmount(Double totalSgstAmount) {
        this.totalSgstAmount = totalSgstAmount;
    }
}