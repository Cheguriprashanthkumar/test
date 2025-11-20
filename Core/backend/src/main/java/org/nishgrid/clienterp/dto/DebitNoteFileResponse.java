package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.DebitNoteFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Data
public class DebitNoteFileResponse {
    private Long id;
    private Long debitNoteId;
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private String uploadedBy;

    public static DebitNoteFileResponse fromEntity(DebitNoteFile doc) {
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/debit-note-files/download/") // Adjust path as per your controller
                .path(doc.getFilePath())
                .toUriString();

        DebitNoteFileResponse response = new DebitNoteFileResponse();
        response.setId(doc.getId());
        response.setDebitNoteId(doc.getDebitNote().getDebitNoteId());
        response.setFileName(doc.getFilePath());
        response.setFileDownloadUri(fileDownloadUri);
        response.setFileType(doc.getFileType());
        response.setUploadedBy(doc.getUploadedBy());
        return response;
    }
}