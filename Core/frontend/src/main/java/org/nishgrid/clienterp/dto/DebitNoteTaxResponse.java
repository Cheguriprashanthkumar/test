package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

// Add this annotation to ignore any other new fields the server might send in the future
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitNoteTaxResponse {

    // --- ADD THIS FIELD ---
    private Long id;

    private String taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    // --- ADD GETTER AND SETTER FOR THE NEW FIELD ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Existing Getters and Setters
    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount.setScale(2);
    }
}