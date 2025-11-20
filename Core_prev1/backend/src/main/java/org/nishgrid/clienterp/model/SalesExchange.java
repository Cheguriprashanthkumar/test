package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_exchanges")
public class SalesExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exchangeId;

    // The item that was returned
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_item_id", nullable = false, unique = true)
    private SalesItem returnedItem;

    // This would link to a master product/item table. Using Long for now.
    @Column(name = "new_item_id", nullable = false)
    private Long newItemId;

    private Double differenceAmount; // Positive if customer pays, negative if refunded
    private LocalDateTime exchangeDate;

    @Column(columnDefinition = "TEXT")
    private String remarks;
    private String handledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_invoice_id", nullable = false)
    private SalesInvoice originalInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Long getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public SalesItem getReturnedItem() {
        return returnedItem;
    }

    public void setReturnedItem(SalesItem returnedItem) {
        this.returnedItem = returnedItem;
    }

    public Long getNewItemId() {
        return newItemId;
    }

    public void setNewItemId(Long newItemId) {
        this.newItemId = newItemId;
    }

    public Double getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(Double differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public LocalDateTime getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(LocalDateTime exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }

    public SalesInvoice getOriginalInvoice() {
        return originalInvoice;
    }

    public void setOriginalInvoice(SalesInvoice originalInvoice) {
        this.originalInvoice = originalInvoice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}