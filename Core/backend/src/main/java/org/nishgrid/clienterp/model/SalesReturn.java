package org.nishgrid.clienterp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_returns")
public class SalesReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnId;

    private LocalDate returnDate;

    @Column(columnDefinition = "TEXT")
    private String returnReason;

    private Integer quantity;

    @Column(precision = 15, scale = 2) // ensures proper decimal storage
    private BigDecimal returnAmount;

    private String refundMode;
    private String handledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private SalesInvoice salesInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private SalesItem salesItem;

    public Long getReturnId() { return returnId; }
    public void setReturnId(Long returnId) { this.returnId = returnId; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }

    public Integer getQuantity() { return quantity != null ? quantity : 0; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getReturnAmount() { return returnAmount; }
    public void setReturnAmount(BigDecimal returnAmount) { this.returnAmount = returnAmount; }

    public String getRefundMode() { return refundMode; }
    public void setRefundMode(String refundMode) { this.refundMode = refundMode; }

    public String getHandledBy() { return handledBy; }
    public void setHandledBy(String handledBy) { this.handledBy = handledBy; }

    public SalesInvoice getSalesInvoice() { return salesInvoice; }
    public void setSalesInvoice(SalesInvoice salesInvoice) { this.salesInvoice = salesInvoice; }

    public SalesItem getSalesItem() { return salesItem; }
    public void setSalesItem(SalesItem salesItem) { this.salesItem = salesItem; }
}
