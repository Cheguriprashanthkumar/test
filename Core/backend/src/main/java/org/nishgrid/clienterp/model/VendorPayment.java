package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments_to_vendors")
@Getter
@Setter
public class VendorPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    private LocalDate paymentDate;

    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String referenceNo;

    @Lob
    private String remarks;

    public enum PaymentMode {
        CASH, BANK, UPI, CHEQUE
    }
}