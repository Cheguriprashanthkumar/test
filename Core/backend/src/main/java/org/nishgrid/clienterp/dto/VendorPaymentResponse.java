package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.VendorPayment;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VendorPaymentResponse {
    private Long id;
    private Long vendorId;
    private String vendorName;
    private LocalDate paymentDate;
    private BigDecimal amountPaid;
    private VendorPayment.PaymentMode paymentMode;
    private String referenceNo;
    private String remarks;

    public static VendorPaymentResponse fromEntity(VendorPayment payment) {
        VendorPaymentResponse dto = new VendorPaymentResponse();
        dto.setId(payment.getId());
        dto.setVendorId(payment.getVendor().getId());
        dto.setVendorName(payment.getVendor().getName());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmountPaid(payment.getAmountPaid());
        dto.setPaymentMode(payment.getPaymentMode());
        dto.setReferenceNo(payment.getReferenceNo());
        dto.setRemarks(payment.getRemarks());
        return dto;
    }
}