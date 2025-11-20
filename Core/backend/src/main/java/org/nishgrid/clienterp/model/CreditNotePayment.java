package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "credit_note_payments")
public class CreditNotePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_note_id", nullable = false)
    @JsonIgnore
    private CreditNote creditNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementType settlementType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate settlementDate;

    private String referenceNumber;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum SettlementType {
        Cash,
        Bank_Transfer,
        Adjustment_in_Next_Purchase
    }
}
