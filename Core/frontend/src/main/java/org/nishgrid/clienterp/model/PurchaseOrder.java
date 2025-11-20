package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PurchaseOrder {
    private final long id;
    private final String poNumber;
    private final Vendor vendor;
    private final LocalDate orderDate;
    private final String status;
    private final BigDecimal totalAmount;
    private final String remarks;
    private final LocalDateTime createdAt;
    private final List<PurchaseOrderItem> items;


    public enum PoStatus {
        PENDING, RECEIVED, CANCELLED
    }

    @JsonCreator
    public PurchaseOrder(@JsonProperty("id") long id,
                         @JsonProperty("poNumber") String poNumber,
                         @JsonProperty("vendor") Vendor vendor,
                         @JsonProperty("orderDate") LocalDate orderDate,
                         @JsonProperty("status") String status,
                         @JsonProperty("totalAmount") BigDecimal totalAmount,
                         @JsonProperty("remarks") String remarks,
                         @JsonProperty("createdAt") LocalDateTime createdAt,
                         @JsonProperty("items") List<PurchaseOrderItem> items) {
        this.id = id;
        this.poNumber = poNumber;
        this.vendor = vendor;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.items = items;
    }

    public long getId() { return id; }
    public String getPoNumber() { return poNumber; }
    public Vendor getVendor() { return vendor; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<PurchaseOrderItem> getItems() { return items; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}