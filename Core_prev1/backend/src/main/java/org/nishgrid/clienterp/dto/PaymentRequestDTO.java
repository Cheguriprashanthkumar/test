package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequestDTO {
    private BigDecimal amount;
    private String mode;
    private String referenceNo;
    private String receivedBy;
    private LocalDate paymentDate;
}