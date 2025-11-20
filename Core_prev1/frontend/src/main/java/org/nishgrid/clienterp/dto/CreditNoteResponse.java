package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.nishgrid.clienterp.model.CreditNote;
import org.nishgrid.clienterp.model.CreditNotePayment;
import org.nishgrid.clienterp.model.CreditNoteTax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditNoteResponse {
    // Main credit note fields
    private Long creditNoteId;
    private String creditNoteNumber;
    private LocalDate creditNoteDate;
    private Long customerId;
    private String customerName;
    private Long originalInvoiceId;
    private String originalInvoiceNumber;
    private String reason;
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private BigDecimal totalAmountIncludingTax;
    private String currency;
    private CreditNote.CreditNoteStatus status;
    private String issuedBy;
    private String approvedBy;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private List<CreditNoteItemResponse> items;
    private List<CreditNoteTaxResponse> taxes;
    private List<CreditNotePaymentResponse> payments;
    private List<CreditNoteFileResponse> files;

    // --- FIX STARTS HERE ---
    // Explicitly add the getter to resolve the compile error
    public List<CreditNoteFileResponse> getFiles() {
        return this.files;
    }
    // --- FIX ENDS HERE ---

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreditNoteItemResponse {
        private Long itemId;
        private Long productId;
        private String productName;
        private String hsnCode;
        private BigDecimal purity;
        private BigDecimal weight;
        private Integer quantity;
        private BigDecimal ratePerGram;
        private BigDecimal discountAmount;
        private BigDecimal taxableAmount;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreditNoteTaxResponse {
        private Long taxId;
        private CreditNoteTax.TaxType taxType;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreditNotePaymentResponse {
        private Long paymentId;
        private CreditNotePayment.SettlementType settlementType;
        private BigDecimal amount;
        private LocalDate settlementDate;
        private String referenceNumber;
        private LocalDateTime createdAt;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreditNoteFileResponse {
        private Long fileId;
        private String fileName;
        private String fileType;
        private Long fileSize;
        private String uploadedBy;
        private LocalDateTime uploadedAt;
    }
}