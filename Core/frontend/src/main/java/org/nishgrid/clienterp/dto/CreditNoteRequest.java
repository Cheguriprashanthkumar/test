package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.CreditNote.CreditNoteStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreditNoteRequest {

    private LocalDate creditNoteDate;
    private Long customerId;
    private Long originalInvoiceId;
    private String reason;
    private CreditNoteStatus status;
    private String issuedBy;
    private String approvedBy;
    private String remarks;
    private String currency = "INR";


    private List<CreditNoteItemDTO> items;

    @Data
    public static class CreditNoteItemDTO {
        private Long productId;
        private String description;
        private String hsnCode;
        private BigDecimal purity;
        private BigDecimal weight;
        private Integer quantity;
        private BigDecimal ratePerGram;
        private BigDecimal discountAmount;
        private BigDecimal taxRate;
    }
}