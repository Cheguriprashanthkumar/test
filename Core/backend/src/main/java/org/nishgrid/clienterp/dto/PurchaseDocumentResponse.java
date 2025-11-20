package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.PurchaseDocument;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Data
public class PurchaseDocumentResponse {
    private Long id;
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private Long purchaseInvoiceId;

    public static PurchaseDocumentResponse fromEntity(PurchaseDocument doc) {
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/purchase-documents/download/")
                .path(doc.getFileName())
                .toUriString();

        PurchaseDocumentResponse response = new PurchaseDocumentResponse();
        response.setId(doc.getId());
        response.setFileName(doc.getFileName());
        response.setFileType(doc.getFileType());
        response.setPurchaseInvoiceId(doc.getPurchaseInvoice().getId());
        response.setFileDownloadUri(fileDownloadUri);
        return response;
    }
}