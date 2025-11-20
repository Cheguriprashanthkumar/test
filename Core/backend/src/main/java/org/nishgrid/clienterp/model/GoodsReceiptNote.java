package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "grn")
@Getter
@Setter
public class GoodsReceiptNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String grnNumber;

    @OneToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    private LocalDate receivedDate;

    private String receivedBy;

    @Lob
    private String remarks;
}