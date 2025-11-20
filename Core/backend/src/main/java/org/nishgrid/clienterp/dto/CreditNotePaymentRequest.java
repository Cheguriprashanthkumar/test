package org.nishgrid.clienterp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.nishgrid.clienterp.model.CreditNotePayment.SettlementType;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreditNotePaymentRequest {

    @NotNull(message = "Settlement Type cannot be null")
    private SettlementType settlementType;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Settlement Date cannot be null")
    private LocalDate settlementDate;

    private String referenceNumber;
}