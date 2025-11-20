package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.CreditNotePayment;
import org.nishgrid.clienterp.model.CreditNotePayment.SettlementType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreditNotePaymentResponse {
    private Long paymentId;
    private SettlementType settlementType;
    private BigDecimal amount;
    private LocalDate settlementDate;
    private String referenceNumber;
    private LocalDateTime createdAt;

    public static CreditNotePaymentResponse fromEntity(CreditNotePayment payment) {
        CreditNotePaymentResponse dto = new CreditNotePaymentResponse();
        dto.setPaymentId(payment.getPaymentId());
        dto.setSettlementType(payment.getSettlementType());
        dto.setAmount(payment.getAmount());
        dto.setSettlementDate(payment.getSettlementDate());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}