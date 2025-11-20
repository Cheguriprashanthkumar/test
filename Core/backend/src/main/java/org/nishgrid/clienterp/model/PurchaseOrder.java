package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@EqualsAndHashCode(of = "id") // Base equality only on the ID
@ToString(exclude = "items")  // Prevent recursion when printing
public class PurchaseOrder {
    // ... all fields remain the same ...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String poNumber;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private PoStatus status;

    private BigDecimal totalAmount;

    @Lob
    private String remarks;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    public void addItem(PurchaseOrderItem item) {
        this.items.add(item);
        item.setPurchaseOrder(this);
    }

    public enum PoStatus {
        PENDING, RECEIVED, CANCELLED
    }
}