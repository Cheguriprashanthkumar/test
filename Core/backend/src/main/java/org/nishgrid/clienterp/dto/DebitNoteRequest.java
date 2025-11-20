package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.DebitNote;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class DebitNoteRequest {
    private String debitNoteNo;
    private Long vendorId;
    private Long purchaseInvoiceId;
    private LocalDate debitNoteDate;
    private String reason;
    private DebitNote.DebitNoteStatus status;
    private String createdBy;
    private String approvedBy;
    private List<DebitNoteItemDTO> items;

    @Data
    public static class DebitNoteItemDTO {
        private Long itemId;
        private String hsnCode;
        private String purity;
        private BigDecimal weight;
        private Integer qty;
        private BigDecimal unitRate;
        private BigDecimal gstRate;
    }
}