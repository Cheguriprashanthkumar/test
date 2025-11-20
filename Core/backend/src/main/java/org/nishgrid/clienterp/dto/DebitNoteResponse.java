package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.DebitNote;
import org.nishgrid.clienterp.model.DebitNoteItem;
import org.nishgrid.clienterp.model.DebitNoteTax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DebitNoteResponse {
    private Long debitNoteId;
    private String debitNoteNo;
    private Long vendorId;
    private String vendorName;
    private Long purchaseInvoiceId;
    private String purchaseInvoiceNumber;
    private LocalDate debitNoteDate;
    private String reason;
    private BigDecimal totalAmount;
    private BigDecimal totalGstAmount;
    private BigDecimal totalAmountWithGst;
    private DebitNote.DebitNoteStatus status;
    private String createdBy;
    private String approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DebitNoteItemResponse> items;
    private List<DebitNoteTaxResponse> taxes;

    public static DebitNoteResponse fromEntity(DebitNote debitNote) {
        DebitNoteResponse dto = new DebitNoteResponse();
        dto.setDebitNoteId(debitNote.getDebitNoteId());
        dto.setDebitNoteNo(debitNote.getDebitNoteNo());
        dto.setVendorId(debitNote.getVendor().getId());
        dto.setVendorName(debitNote.getVendor().getName());
        if (debitNote.getPurchaseInvoice() != null) {
            dto.setPurchaseInvoiceId(debitNote.getPurchaseInvoice().getId());
            dto.setPurchaseInvoiceNumber(debitNote.getPurchaseInvoice().getInvoiceNumber());
        }
        dto.setDebitNoteDate(debitNote.getDebitNoteDate());
        dto.setReason(debitNote.getReason());
        dto.setTotalAmount(debitNote.getTotalAmount());
        dto.setTotalGstAmount(debitNote.getTotalGstAmount());
        dto.setTotalAmountWithGst(debitNote.getTotalAmountWithGst());
        dto.setStatus(debitNote.getStatus());
        dto.setCreatedBy(debitNote.getCreatedBy());
        dto.setApprovedBy(debitNote.getApprovedBy());
        dto.setCreatedAt(debitNote.getCreatedAt());
        dto.setUpdatedAt(debitNote.getUpdatedAt());
        dto.setItems(debitNote.getItems().stream()
                .map(DebitNoteItemResponse::fromEntity)
                .collect(Collectors.toList()));
        dto.setTaxes(debitNote.getTaxes().stream()
                .map(DebitNoteTaxResponse::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }

    @Data
    public static class DebitNoteItemResponse {
        private Long id;
        private Long itemId;
        private String itemName;
        private String hsnCode;
        private String purity;
        private BigDecimal weight;
        private Integer qty;
        private BigDecimal unitRate;
        private BigDecimal gstRate;
        private BigDecimal gstAmount;
        private BigDecimal lineTotal;
        private BigDecimal totalWithGst;

        public static DebitNoteItemResponse fromEntity(DebitNoteItem item) {
            DebitNoteItemResponse dto = new DebitNoteItemResponse();
            dto.setId(item.getId());
            if (item.getItem() != null) {
                dto.setItemId(item.getItem().getId());
                dto.setItemName(item.getItem().getName());
            }
            dto.setHsnCode(item.getHsnCode());
            dto.setPurity(item.getPurity());
            dto.setWeight(item.getWeight());
            dto.setQty(item.getQty());
            dto.setUnitRate(item.getUnitRate());
            dto.setGstRate(item.getGstRate());
            dto.setGstAmount(item.getGstAmount());
            dto.setLineTotal(item.getLineTotal());
            dto.setTotalWithGst(item.getTotalWithGst());
            return dto;
        }
    }

    @Data
    public static class DebitNoteTaxResponse {
        private Long id;
        private DebitNoteTax.TaxType taxType;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;

        public static DebitNoteTaxResponse fromEntity(DebitNoteTax tax) {
            DebitNoteTaxResponse dto = new DebitNoteTaxResponse();
            dto.setId(tax.getId());
            dto.setTaxType(tax.getTaxType());
            dto.setTaxRate(tax.getTaxRate());
            dto.setTaxAmount(tax.getTaxAmount());
            return dto;
        }
    }
}