package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.nishgrid.clienterp.model.DebitNote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// This annotation prevents crashes if the server adds new fields in the future.
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitNoteResponse {

    // All fields from the server response
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
    private List<DebitNoteTaxResponse> taxes; // The field that was causing the error

    // Getters and Setters for all fields...
    public Long getDebitNoteId() { return debitNoteId; }
    public void setDebitNoteId(Long debitNoteId) { this.debitNoteId = debitNoteId; }
    public String getDebitNoteNo() { return debitNoteNo; }
    public void setDebitNoteNo(String debitNoteNo) { this.debitNoteNo = debitNoteNo; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public Long getPurchaseInvoiceId() { return purchaseInvoiceId; }
    public void setPurchaseInvoiceId(Long purchaseInvoiceId) { this.purchaseInvoiceId = purchaseInvoiceId; }
    public String getPurchaseInvoiceNumber() { return purchaseInvoiceNumber; }
    public void setPurchaseInvoiceNumber(String purchaseInvoiceNumber) { this.purchaseInvoiceNumber = purchaseInvoiceNumber; }
    public LocalDate getDebitNoteDate() { return debitNoteDate; }
    public void setDebitNoteDate(LocalDate debitNoteDate) { this.debitNoteDate = debitNoteDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getTotalGstAmount() { return totalGstAmount; }
    public void setTotalGstAmount(BigDecimal totalGstAmount) { this.totalGstAmount = totalGstAmount; }
    public BigDecimal getTotalAmountWithGst() { return totalAmountWithGst; }
    public void setTotalAmountWithGst(BigDecimal totalAmountWithGst) { this.totalAmountWithGst = totalAmountWithGst; }
    public DebitNote.DebitNoteStatus getStatus() { return status; }
    public void setStatus(DebitNote.DebitNoteStatus status) { this.status = status; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<DebitNoteItemResponse> getItems() { return items; }
    public void setItems(List<DebitNoteItemResponse> items) { this.items = items; }
    public List<DebitNoteTaxResponse> getTaxes() { return taxes; }
    public void setTaxes(List<DebitNoteTaxResponse> taxes) { this.taxes = taxes; }

    // Nested class to hold item details from the response
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DebitNoteItemResponse {
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

        // Getters and Setters for item fields...
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public String getHsnCode() { return hsnCode; }
        public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }
        public String getPurity() { return purity; }
        public void setPurity(String purity) { this.purity = purity; }
        public BigDecimal getWeight() { return weight; }
        public void setWeight(BigDecimal weight) { this.weight = weight; }
        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
        public BigDecimal getUnitRate() { return unitRate; }
        public void setUnitRate(BigDecimal unitRate) { this.unitRate = unitRate; }
        public BigDecimal getGstRate() { return gstRate; }
        public void setGstRate(BigDecimal gstRate) { this.gstRate = gstRate; }
        public BigDecimal getGstAmount() { return gstAmount; }
        public void setGstAmount(BigDecimal gstAmount) { this.gstAmount = gstAmount; }
        public BigDecimal getLineTotal() { return lineTotal; }
        public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
        public BigDecimal getTotalWithGst() { return totalWithGst; }
        public void setTotalWithGst(BigDecimal totalWithGst) { this.totalWithGst = totalWithGst; }
    }
}