package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftVoucherResponseDTO {
    private Long id;
    private String voucherCode;
    private Long customerId;
    private String customerName;
    private BigDecimal value;
    private String status; // Enums are deserialized as Strings by default

    // Getters and Setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}