package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.DebitNoteAuditLog;
import java.time.LocalDateTime;

@Data
public class DebitNoteAuditLogResponse {
    private Long id;
    private DebitNoteResponse debitNote;
    private String action;
    private String oldValue;
    private String newValue;
    private String changedBy;
    private LocalDateTime changedAt;

    public static DebitNoteAuditLogResponse fromEntity(DebitNoteAuditLog log) {
        DebitNoteAuditLogResponse dto = new DebitNoteAuditLogResponse();
        dto.setId(log.getId());

        // Use the fromEntity method of DebitNoteResponse to create the nested object
        if (log.getDebitNote() != null) {
            dto.setDebitNote(DebitNoteResponse.fromEntity(log.getDebitNote()));
        }

        dto.setAction(log.getAction().toString());
        dto.setOldValue(log.getOldValue());
        dto.setNewValue(log.getNewValue());
        dto.setChangedBy(log.getChangedBy());
        dto.setChangedAt(log.getChangedAt());
        return dto;
    }
}