package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.CreditNoteFile;
import java.time.LocalDateTime;

@Data
public class CreditNoteFileResponse {
    private Long fileId;
    private String filePath;
    private String fileType;
    private LocalDateTime uploadedAt;

    public static CreditNoteFileResponse fromEntity(CreditNoteFile file) {
        CreditNoteFileResponse dto = new CreditNoteFileResponse();
        dto.setFileId(file.getFileId());
        dto.setFilePath(file.getFilePath());
        dto.setFileType(file.getFileType());
        dto.setUploadedAt(file.getUploadedAt());
        return dto;
    }
}