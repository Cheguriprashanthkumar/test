package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "credit_note_taxes")
public class CreditNoteTax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_note_id", nullable = false)
    @JsonIgnore
    private CreditNote creditNote;

    @Enumerated(EnumType.STRING)
    private TaxType taxType;

    @Column(precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(precision = 18, scale = 2)
    private BigDecimal taxAmount;

    public enum TaxType {
        CGST, SGST, IGST
    }
}