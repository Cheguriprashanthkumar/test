package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"items", "taxes", "payments", "files"}) // Excludes collections from toString()
@Entity
@Table(name = "credit_notes")
public class CreditNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditNoteId;

    @Column(unique = true, nullable = false, length = 50)
    private String creditNoteNumber;

    @Column(nullable = false)
    private LocalDate creditNoteDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_invoice_id")
    private SalesInvoice originalInvoice;

    @Column(nullable = false)
    private String reason;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalTax;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalAmountIncludingTax;

    @Column(length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditNoteStatus status;

    private String issuedBy;
    private String approvedBy;

    @Lob
    private String remarks;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "creditNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CreditNoteItem> items = new HashSet<>();

    @OneToMany(mappedBy = "creditNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CreditNoteTax> taxes = new HashSet<>();

    @OneToMany(mappedBy = "creditNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CreditNotePayment> payments = new HashSet<>();

    @OneToMany(mappedBy = "creditNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CreditNoteFile> files = new HashSet<>();

    // Helper methods to keep relationships in sync
    public void addItem(CreditNoteItem item) {
        items.add(item);
        item.setCreditNote(this);
    }

    public void addTax(CreditNoteTax tax) {
        taxes.add(tax);
        tax.setCreditNote(this);
    }

    public void addPayment(CreditNotePayment payment) {
        payments.add(payment);
        payment.setCreditNote(this);
    }

    public void addFile(CreditNoteFile file) {
        files.add(file);
        file.setCreditNote(this);
    }

    public enum CreditNoteStatus {
        Draft, Approved, Settled
    }

    // Manually implement equals() and hashCode() using only the ID to prevent recursion
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditNote that = (CreditNote) o;
        return Objects.equals(creditNoteId, that.creditNoteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creditNoteId);
    }
}