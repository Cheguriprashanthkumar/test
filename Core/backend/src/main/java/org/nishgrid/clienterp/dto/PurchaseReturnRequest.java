package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseReturnRequest {
    private String returnNumber;
    private Long vendorId;
    private Long purchaseInvoiceId;
    private LocalDate returnDate;
    private String reason;
    private BigDecimal amountReturned;
}