package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.GoodsReceiptNote;
import java.time.LocalDate;

@Data
public class GrnResponse {
    private Long id;
    private String grnNumber;
    private Long purchaseOrderId;
    private String poNumber;
    private String vendorName;
    private LocalDate receivedDate;
    private String receivedBy;
    private String remarks;

    // A helper method to easily convert an Entity to this DTO
    public static GrnResponse fromEntity(GoodsReceiptNote grn) {
        GrnResponse dto = new GrnResponse();
        dto.setId(grn.getId());
        dto.setGrnNumber(grn.getGrnNumber());
        dto.setPurchaseOrderId(grn.getPurchaseOrder().getId());
        dto.setPoNumber(grn.getPurchaseOrder().getPoNumber());
        dto.setVendorName(grn.getPurchaseOrder().getVendor().getName());
        dto.setReceivedDate(grn.getReceivedDate());
        dto.setReceivedBy(grn.getReceivedBy());
        dto.setRemarks(grn.getRemarks());
        return dto;
    }
}