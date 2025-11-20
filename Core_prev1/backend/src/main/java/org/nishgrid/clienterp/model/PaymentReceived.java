package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal; // Import this
import java.time.LocalDate;

@Entity
@Table(name = "payments_received")
@Getter
@Setter
public class PaymentReceived {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private LocalDate paymentDate;
    private BigDecimal amount; // Changed from Double
    private String mode;
    private String referenceNo;
    private String receivedBy;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private SalesInvoice invoice;
}