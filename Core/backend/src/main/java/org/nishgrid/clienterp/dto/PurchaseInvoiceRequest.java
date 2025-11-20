package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseInvoiceRequest {
    private String invoiceNumber;
    private Long vendorId;
    private Long purchaseOrderId;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal gstAmount;
    private BigDecimal grandTotal;
}