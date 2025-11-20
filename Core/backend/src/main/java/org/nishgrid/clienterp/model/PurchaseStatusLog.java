package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_status_logs")
@Getter
@Setter
public class PurchaseStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    private String oldStatus;

    private String newStatus;

    private String changedBy; // User who changed the status

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime changedAt;
}