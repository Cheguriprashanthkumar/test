package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;

public class GiftVoucherCreateDTO {
    private Long customerId;
    private BigDecimal value;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}