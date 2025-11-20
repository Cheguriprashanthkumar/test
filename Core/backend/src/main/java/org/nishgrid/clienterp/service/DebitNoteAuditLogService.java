package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.DebitNoteResponse;
import org.nishgrid.clienterp.model.DebitNote;
import org.nishgrid.clienterp.model.DebitNoteAuditLog;
import org.nishgrid.clienterp.repository.DebitNoteAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DebitNoteAuditLogService {

    @Autowired private DebitNoteAuditLogRepository auditLogRepository;
    @Autowired private AuditComparisonService comparisonService;

    private void log(DebitNote debitNote, DebitNoteAuditLog.AuditAction action, String oldValue, String newValue, String changedBy) {
        DebitNoteAuditLog log = new DebitNoteAuditLog();
        log.setDebitNote(debitNote);
        log.setAction(action);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangedBy(changedBy);
        auditLogRepository.save(log);
    }

    public void logCreation(DebitNote debitNote, String changedBy) {
        String details = "Debit Note " + debitNote.getDebitNoteNo() + " was created.";
        log(debitNote, DebitNoteAuditLog.AuditAction.CREATED, null, details, changedBy);
    }

    public void logUpdate(DebitNoteResponse oldState, DebitNote newState, String changedBy) {
        AuditComparisonService.ChangeSummary summary = comparisonService.compare(oldState, DebitNoteResponse.fromEntity(newState));

        if (!summary.isEmpty()) {
            log(newState, DebitNoteAuditLog.AuditAction.UPDATED, summary.getOldValuesAsString(), summary.getNewValuesAsString(), changedBy);
        }
    }

    public void logDeletion(DebitNote debitNote, String changedBy) {
        String details = "Debit Note " + debitNote.getDebitNoteNo() + " was deleted.";
        log(debitNote, DebitNoteAuditLog.AuditAction.DELETED, details, null, changedBy);
    }
}