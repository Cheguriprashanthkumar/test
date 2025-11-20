package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.DebitNote;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    // Getters and Setters
    public String getDebitNoteNo() {
        return debitNoteNo;
    }

    public void setDebitNoteNo(String debitNoteNo) {
        this.debitNoteNo = debitNoteNo;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getPurchaseInvoiceId() {
        return purchaseInvoiceId;
    }

    public void setPurchaseInvoiceId(Long purchaseInvoiceId) {
        this.purchaseInvoiceId = purchaseInvoiceId;
    }

    public LocalDate getDebitNoteDate() {
        return debitNoteDate;
    }

    public void setDebitNoteDate(LocalDate debitNoteDate) {
        this.debitNoteDate = debitNoteDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public DebitNote.DebitNoteStatus getStatus() {
        return status;
    }

    public void setStatus(DebitNote.DebitNoteStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public List<DebitNoteItemDTO> getItems() {
        return items;
    }

    public void setItems(List<DebitNoteItemDTO> items) {
        this.items = items;
    }

    public static class DebitNoteItemDTO {
        private Long itemId;
        private String hsnCode;
        private String purity;
        private BigDecimal weight;
        private Integer qty;
        private BigDecimal unitRate;
        private BigDecimal gstRate;
        private BigDecimal gstAmount;
        private BigDecimal lineTotal;
        private BigDecimal totalWithGst;

        // Getters and Setters for DebitNoteItemDTO
        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public String getHsnCode() {
            return hsnCode;
        }

        public void setHsnCode(String hsnCode) {
            this.hsnCode = hsnCode;
        }

        public String getPurity() {
            return purity;
        }

        public void setPurity(String purity) {
            this.purity = purity;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public BigDecimal getUnitRate() {
            return unitRate;
        }

        public void setUnitRate(BigDecimal unitRate) {
            this.unitRate = unitRate;
        }

        public BigDecimal getGstRate() {
            return gstRate;
        }

        public void setGstRate(BigDecimal gstRate) {
            this.gstRate = gstRate;
        }

        public BigDecimal getGstAmount() {
            return gstAmount;
        }

        public void setGstAmount(BigDecimal gstAmount) {
            this.gstAmount = gstAmount;
        }

        public BigDecimal getLineTotal() {
            return lineTotal;
        }

        public void setLineTotal(BigDecimal lineTotal) {
            this.lineTotal = lineTotal;
        }

        public BigDecimal getTotalWithGst() {
            return totalWithGst;
        }

        public void setTotalWithGst(BigDecimal totalWithGst) {
            this.totalWithGst = totalWithGst;
        }
    }
}