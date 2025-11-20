package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.VendorPayment;
import java.math.BigDecimal;
import java.time.LocalDate;

public class VendorPaymentRequest {
    private Long vendorId;
    private LocalDate paymentDate;
    private BigDecimal amountPaid;
    private VendorPayment.PaymentMode paymentMode;
    private String referenceNo;
    private String remarks;

    // Getters and Setters
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public VendorPayment.PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(VendorPayment.PaymentMode paymentMode) { this.paymentMode = paymentMode; }
    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}