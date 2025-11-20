package org.nishgrid.clienterp.service;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.CreditNoteAuditLogResponse; // <-- ADD IMPORT
import org.nishgrid.clienterp.model.CreditNote;
import org.nishgrid.clienterp.model.CreditNoteAuditLog;
import org.nishgrid.clienterp.repository.CreditNoteAuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // <-- ADD IMPORT
import java.util.stream.Collectors; // <-- ADD IMPORT

@Service
@Transactional
@RequiredArgsConstructor
public class CreditNoteAuditLogService {

    private final CreditNoteAuditLogRepository auditLogRepository;

    // --- ADD THIS METHOD ---
    @Transactional(readOnly = true)
    public List<CreditNoteAuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(CreditNoteAuditLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void logCreation(CreditNote note, String user) {
        log(note.getCreditNoteId(), user, "Credit Note created with number: " + note.getCreditNoteNumber());
    }

    public void logUpdate(CreditNote oldNote, CreditNote newNote, String user) {
        String description = String.format("Credit Note updated. Status changed from %s to %s.",
                oldNote.getStatus(), newNote.getStatus());
        log(newNote.getCreditNoteId(), user, description);
    }

    public void logDeletion(CreditNote note, String user) {
        log(note.getCreditNoteId(), user, "Credit Note deleted: " + note.getCreditNoteNumber());
    }

    public void log(Long noteId, String user, String description) {
        CreditNoteAuditLog log = new CreditNoteAuditLog();
        log.setCreditNoteId(noteId);
        log.setChangedBy(user);
        log.setChangeDescription(description);
        auditLogRepository.save(log);
    }
}