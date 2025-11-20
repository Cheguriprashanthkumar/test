package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.GiftVoucher;
import org.nishgrid.clienterp.model.VoucherStatus;
import java.math.BigDecimal;

public class GiftVoucherResponseDTO {
    private Long id;
    private String voucherCode;
    private Long customerId;
    private String customerName;
    private BigDecimal value;
    private VoucherStatus status;

    public static GiftVoucherResponseDTO fromEntity(GiftVoucher gv) {
        GiftVoucherResponseDTO dto = new GiftVoucherResponseDTO();
        dto.setId(gv.getId());
        dto.setVoucherCode(gv.getVoucherCode());
        dto.setValue(gv.getValue());
        dto.setStatus(gv.getStatus());
        if (gv.getCustomer() != null) {
            dto.setCustomerId(gv.getCustomer().getCustomerId());
            dto.setCustomerName(gv.getCustomer().getName());
        }
        return dto;
    }

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
    public VoucherStatus getStatus() { return status; }
    public void setStatus(VoucherStatus status) { this.status = status; }
}