package org.nishgrid.clienterp.dto;

import java.time.LocalDateTime;

public class DebitNoteAuditLogResponse {
    private Long id;
    private DebitNoteResponse debitNote;
    private String debitNoteNumber; // Add this missing field
    private String action;
    private String oldValue;
    private String newValue;
    private String changedBy;
    private LocalDateTime changedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DebitNoteResponse getDebitNote() { return debitNote; }
    public void setDebitNote(DebitNoteResponse debitNote) { this.debitNote = debitNote; }
    public String getDebitNoteNumber() { return debitNoteNumber; }
    public void setDebitNoteNumber(String debitNoteNumber) { this.debitNoteNumber = debitNoteNumber; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}