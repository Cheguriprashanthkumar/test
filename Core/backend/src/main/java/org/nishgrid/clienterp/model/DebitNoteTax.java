package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "debit_note_taxes")
@Getter
@Setter
public class DebitNoteTax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_note_id", nullable = false)
    @JsonIgnore
    private DebitNote debitNote;

    @Enumerated(EnumType.STRING)
    private TaxType taxType;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    public enum TaxType {
        CGST, SGST, IGST
    }
}