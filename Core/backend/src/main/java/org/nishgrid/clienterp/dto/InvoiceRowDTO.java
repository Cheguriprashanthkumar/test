package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceRowDTO {
    private Long id;
    private String invoiceNo;
    private String customerName;
    private LocalDate invoiceDate;
    private String salesType;
    private String paymentMode;
    private BigDecimal totalAmount;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private String status;
    private BigDecimal oldGoldValue;
}