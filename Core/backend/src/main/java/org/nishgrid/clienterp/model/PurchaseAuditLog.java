package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_audit_logs")
@Getter
@Setter
public class PurchaseAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String actionType;

    private String module;

    private String details;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}