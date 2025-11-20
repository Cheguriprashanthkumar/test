package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponseDTO {
    private Long invoiceId;
    private String status;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
}