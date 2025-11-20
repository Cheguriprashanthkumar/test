package org.nishgrid.clienterp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.nishgrid.clienterp.model.CreditNote.CreditNoteStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreditNoteRequest {

    @NotNull(message = "Credit Note Date cannot be null")
    @FutureOrPresent(message = "Credit Note Date cannot be in the past")
    private LocalDate creditNoteDate;

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    private Long originalInvoiceId; // Optional

    @NotEmpty(message = "Reason cannot be empty")
    private String reason;

    @NotNull(message = "Status cannot be null")
    private CreditNoteStatus status;

    @NotEmpty(message = "Issued By cannot be empty")
    private String issuedBy;

    private String approvedBy;
    private String remarks;
    private String currency = "INR";

    @Valid // This enables validation on the nested objects
    @NotEmpty(message = "Credit note must have at least one item.")
    private List<CreditNoteItemDTO> items;

    @Data
    public static class CreditNoteItemDTO {
        @NotNull(message = "Product ID cannot be null")
        private Long productId;
        private String description;
        private String hsnCode;
        private BigDecimal purity;
        private BigDecimal weight;

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotNull(message = "Rate per gram cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be positive")
        private BigDecimal ratePerGram;

        private BigDecimal discountAmount;

        @NotNull(message = "Tax rate cannot be null")
        @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
        private BigDecimal taxRate;
    }
}