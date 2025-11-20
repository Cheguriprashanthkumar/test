package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "debit_note_audit_logs")
@Getter
@Setter
public class DebitNoteAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_note_id", nullable = false)
    private DebitNote debitNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT") // Add this annotation
    private String oldValue;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT") // Add this annotation
    private String newValue;

    @Column(nullable = false)
    private String changedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime changedAt;

    public enum AuditAction {
        CREATED, UPDATED, DELETED, APPROVED
    }
}