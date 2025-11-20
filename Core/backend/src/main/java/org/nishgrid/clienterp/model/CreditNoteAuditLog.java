package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "credit_note_audit_logs")
public class CreditNoteAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(nullable = false)
    private Long creditNoteId;

    private String changedBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String changeDescription;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime changedAt;
}