package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "debit_note_items")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "debitNote")
public class DebitNoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_note_id", nullable = false)
    @JsonIgnore
    private DebitNote debitNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ProductCatalog item;

    private String hsnCode;
    private String purity;
    private BigDecimal weight;
    private Integer qty;
    private BigDecimal unitRate;
    private BigDecimal gstRate;
    private BigDecimal gstAmount;
    private BigDecimal lineTotal;
    private BigDecimal totalWithGst;
}