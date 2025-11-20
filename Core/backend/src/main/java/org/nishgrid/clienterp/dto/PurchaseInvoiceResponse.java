package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.PurchaseInvoice;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseInvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long vendorId;
    private String vendorName;
    private Long purchaseOrderId;
    private String poNumber;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal gstAmount;
    private BigDecimal grandTotal;

    public static PurchaseInvoiceResponse fromEntity(PurchaseInvoice invoice) {
        PurchaseInvoiceResponse dto = new PurchaseInvoiceResponse();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setVendorId(invoice.getVendor().getId());
        dto.setVendorName(invoice.getVendor().getName());
        if (invoice.getPurchaseOrder() != null) {
            dto.setPurchaseOrderId(invoice.getPurchaseOrder().getId());
            dto.setPoNumber(invoice.getPurchaseOrder().getPoNumber());
        }
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setGstAmount(invoice.getGstAmount());
        dto.setGrandTotal(invoice.getGrandTotal());
        return dto;
    }
}