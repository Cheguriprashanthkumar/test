package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;

public class SalesItemSelectionDTO {
    private Long id;
    private String name;
    private int quantityRemaining;
    private BigDecimal unitPrice; // Add this field

    public SalesItemSelectionDTO() {}

    public SalesItemSelectionDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantityRemaining() { return quantityRemaining; }
    public void setQuantityRemaining(int quantityRemaining) { this.quantityRemaining = quantityRemaining; }

    // Add getter and setter for the new field
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}