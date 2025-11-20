package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_invoice_id", nullable = false)
    @JsonBackReference("invoice-payments")
    private SalesInvoice salesInvoice;

    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMode;
    private String referenceNo;
    private String receivedBy;
}