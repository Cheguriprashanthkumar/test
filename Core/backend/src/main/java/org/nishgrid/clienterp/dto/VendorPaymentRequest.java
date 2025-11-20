package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.VendorPayment;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VendorPaymentRequest {
    private Long vendorId;
    private LocalDate paymentDate;
    private BigDecimal amountPaid;
    private VendorPayment.PaymentMode paymentMode;
    private String referenceNo;
    private String remarks;
}