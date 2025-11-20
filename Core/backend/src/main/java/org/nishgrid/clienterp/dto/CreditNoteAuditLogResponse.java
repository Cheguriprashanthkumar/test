package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.CreditNoteAuditLog;
import java.time.LocalDateTime;

@Data
public class CreditNoteAuditLogResponse {
    private Long logId;
    private Long creditNoteId;
    private String changedBy;
    private String changeDescription;
    private LocalDateTime changedAt;

    public static CreditNoteAuditLogResponse fromEntity(CreditNoteAuditLog log) {
        CreditNoteAuditLogResponse dto = new CreditNoteAuditLogResponse();
        dto.setLogId(log.getLogId());
        dto.setCreditNoteId(log.getCreditNoteId());
        dto.setChangedBy(log.getChangedBy());
        dto.setChangeDescription(log.getChangeDescription());
        dto.setChangedAt(log.getChangedAt());
        return dto;
    }
}