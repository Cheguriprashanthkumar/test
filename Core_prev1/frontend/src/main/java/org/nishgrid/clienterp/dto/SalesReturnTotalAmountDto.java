package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;

public class SalesReturnTotalAmountDto {
    private BigDecimal totalReturnedAmount;

    // Add this public, no-argument constructor
    public SalesReturnTotalAmountDto() {
    }

    public SalesReturnTotalAmountDto(BigDecimal totalReturnedAmount) {
        this.totalReturnedAmount = totalReturnedAmount;
    }

    // Getters and Setters
    public BigDecimal getTotalReturnedAmount() {
        return totalReturnedAmount;
    }

    public void setTotalReturnedAmount(BigDecimal totalReturnedAmount) {
        this.totalReturnedAmount = totalReturnedAmount;
    }
}