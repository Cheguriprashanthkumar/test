package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_invoices")
@Getter
@Setter
public class SalesInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @Column(unique = true, nullable = false)
    private String invoiceNo;

    private LocalDate invoiceDate;
    private String paymentMode;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal gstPercent = BigDecimal.ZERO;
    private BigDecimal gstAmount = BigDecimal.ZERO;
    private BigDecimal cgst = BigDecimal.ZERO;
    private BigDecimal sgst = BigDecimal.ZERO;
    private BigDecimal igst = BigDecimal.ZERO;
    private BigDecimal roundOff = BigDecimal.ZERO;
    private BigDecimal netAmount = BigDecimal.ZERO;
    private String status;
    private String remarks;
    private LocalDate createdAt;
    private String salesType;
    private BigDecimal paidAmount = BigDecimal.ZERO;
    private String createdBy;
    private BigDecimal dueAmount = BigDecimal.ZERO;
    private BigDecimal oldGoldValue = BigDecimal.ZERO;

    @OneToMany(mappedBy = "salesInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentTransaction> paymentTransactions = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @JsonManagedReference
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesItem> salesItems = new ArrayList<>();

    public void addPaymentTransaction(PaymentTransaction transaction) {
        paymentTransactions.add(transaction);
        transaction.setSalesInvoice(this);
    }

    public void addSalesItem(SalesItem item) {
        salesItems.add(item);
        item.setInvoice(this);
    }
}