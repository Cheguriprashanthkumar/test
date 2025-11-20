package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.nishgrid.clienterp.model.CreditNote;
import org.nishgrid.clienterp.model.CreditNote.CreditNoteStatus;
import org.nishgrid.clienterp.model.CreditNoteItem;
import org.nishgrid.clienterp.model.CreditNotePayment;
import org.nishgrid.clienterp.model.CreditNoteTax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditNoteResponse {

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
    private CreditNoteStatus status;
    private String issuedBy;
    private String approvedBy;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CreditNoteItemResponse> items;
    private List<CreditNoteTaxResponse> taxes;
    private List<CreditNotePaymentResponse> payments;
    private List<CreditNoteFileResponse> files;
    public static CreditNoteResponse fromEntity(CreditNote creditNote) {
        CreditNoteResponse dto = new CreditNoteResponse();
        dto.setCreditNoteId(creditNote.getCreditNoteId());
        dto.setCreditNoteNumber(creditNote.getCreditNoteNumber());
        dto.setCreditNoteDate(creditNote.getCreditNoteDate());
        dto.setCustomerId(creditNote.getCustomer().getCustomerId());
        dto.setCustomerName(creditNote.getCustomer().getName());
        if (creditNote.getOriginalInvoice() != null) {
            dto.setOriginalInvoiceId(creditNote.getOriginalInvoice().getInvoiceId());
            dto.setOriginalInvoiceNumber(creditNote.getOriginalInvoice().getInvoiceNo());
        }
        dto.setReason(creditNote.getReason());
        dto.setTotalAmount(creditNote.getTotalAmount());
        dto.setTotalTax(creditNote.getTotalTax());
        dto.setTotalAmountIncludingTax(creditNote.getTotalAmountIncludingTax());
        dto.setCurrency(creditNote.getCurrency());
        dto.setStatus(creditNote.getStatus());
        dto.setIssuedBy(creditNote.getIssuedBy());
        dto.setApprovedBy(creditNote.getApprovedBy());
        dto.setRemarks(creditNote.getRemarks());
        dto.setCreatedAt(creditNote.getCreatedAt());
        dto.setUpdatedAt(creditNote.getUpdatedAt());

        dto.setItems(creditNote.getItems().stream()
                .map(CreditNoteItemResponse::fromEntity)
                .collect(Collectors.toList()));

        dto.setTaxes(creditNote.getTaxes().stream()
                .map(CreditNoteTaxResponse::fromEntity)
                .collect(Collectors.toList()));


        dto.setPayments(creditNote.getPayments().stream()
                .map(CreditNotePaymentResponse::fromEntity)
                .collect(Collectors.toList()));

        return dto;
    }

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

        public static CreditNoteItemResponse fromEntity(CreditNoteItem item) {
            CreditNoteItemResponse dto = new CreditNoteItemResponse();
            dto.setItemId(item.getItemId());
            if(item.getProduct() != null) {
                dto.setProductId(item.getProduct().getId());
                dto.setProductName(item.getProduct().getName());
            }
            dto.setHsnCode(item.getHsnCode());
            dto.setPurity(item.getPurity());
            dto.setWeight(item.getWeight());
            dto.setQuantity(item.getQuantity());
            dto.setRatePerGram(item.getRatePerGram());
            dto.setDiscountAmount(item.getDiscountAmount());
            dto.setTaxableAmount(item.getTaxableAmount());
            dto.setTaxRate(item.getTaxRate());
            dto.setTaxAmount(item.getTaxAmount());
            dto.setTotalAmount(item.getTotalAmount());
            return dto;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreditNoteTaxResponse {
        private Long taxId;
        private CreditNoteTax.TaxType taxType;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;

        public static CreditNoteTaxResponse fromEntity(CreditNoteTax tax) {
            CreditNoteTaxResponse dto = new CreditNoteTaxResponse();
            dto.setTaxId(tax.getId());
            dto.setTaxType(tax.getTaxType());
            dto.setTaxRate(tax.getTaxRate());
            dto.setTaxAmount(tax.getTaxAmount());

            return dto;
        }
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
}