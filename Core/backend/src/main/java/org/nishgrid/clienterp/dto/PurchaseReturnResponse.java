package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.PurchaseReturn;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseReturnResponse {
    private Long id;
    private String returnNumber;
    private Long vendorId;
    private String vendorName;
    private Long purchaseInvoiceId;
    private String invoiceNumber;
    private LocalDate returnDate;
    private String reason;
    private BigDecimal amountReturned;

    public static PurchaseReturnResponse fromEntity(PurchaseReturn purchaseReturn) {
        PurchaseReturnResponse dto = new PurchaseReturnResponse();
        dto.setId(purchaseReturn.getId());
        dto.setReturnNumber(purchaseReturn.getReturnNumber());
        dto.setVendorId(purchaseReturn.getVendor().getId());
        dto.setVendorName(purchaseReturn.getVendor().getName());
        if (purchaseReturn.getPurchaseInvoice() != null) {
            dto.setPurchaseInvoiceId(purchaseReturn.getPurchaseInvoice().getId());
            dto.setInvoiceNumber(purchaseReturn.getPurchaseInvoice().getInvoiceNumber());
        }
        dto.setReturnDate(purchaseReturn.getReturnDate());
        dto.setReason(purchaseReturn.getReason());
        dto.setAmountReturned(purchaseReturn.getAmountReturned());
        return dto;
    }
}