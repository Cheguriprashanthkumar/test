package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal; // Ensure this is BigDecimal if your backend uses it
import java.time.LocalDate;

@Data
public class PaymentRequestDTO {

    // THIS LINE IS ESSENTIAL FOR THE ERROR TO GO AWAY
    private Long invoiceId;

    private BigDecimal amount;
    private String mode;
    private String referenceNo;
    private String receivedBy;
    private LocalDate paymentDate;
}