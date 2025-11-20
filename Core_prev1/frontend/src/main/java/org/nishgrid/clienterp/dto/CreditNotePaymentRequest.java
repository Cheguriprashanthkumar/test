package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.CreditNotePayment.SettlementType;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreditNotePaymentRequest {
    private SettlementType settlementType;
    private BigDecimal amount;
    private LocalDate settlementDate;
    private String referenceNumber;
}