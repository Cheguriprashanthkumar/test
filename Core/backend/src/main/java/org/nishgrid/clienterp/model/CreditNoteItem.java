package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "credit_note_items")
public class CreditNoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_note_id", nullable = false)
    private CreditNote creditNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductCatalog product;

    private String description;
    private String hsnCode;
    private BigDecimal purity;
    private BigDecimal weight;
    private Integer quantity;
    private BigDecimal ratePerGram;
    private BigDecimal discountAmount;
    private BigDecimal taxableAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
}