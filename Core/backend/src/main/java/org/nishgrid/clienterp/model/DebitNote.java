package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "debit_notes")
@Getter
@Setter
@EqualsAndHashCode(of = "debitNoteId")
@ToString(exclude = {"items", "taxes"})
public class DebitNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debitNoteId;

    @Column(unique = true, nullable = false)
    private String debitNoteNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_invoice_id")
    private PurchaseInvoice purchaseInvoice;

    @Column(nullable = false)
    private LocalDate debitNoteDate;

    @Column(nullable = false)
    private String reason;

    private BigDecimal totalAmount;
    private BigDecimal totalGstAmount;
    private BigDecimal totalAmountWithGst;

    @Enumerated(EnumType.STRING)
    private DebitNoteStatus status;

    private String createdBy;
    private String approvedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "debitNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebitNoteItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "debitNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebitNoteTax> taxes = new ArrayList<>();

    public void addItem(DebitNoteItem item) {
        this.items.add(item);
        item.setDebitNote(this);
    }
    public void addTax(DebitNoteTax tax) {
        this.taxes.add(tax);
        tax.setDebitNote(this);
    }

    public enum DebitNoteStatus {
        Draft, Approved, Settled
    }
}