package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PurchaseOrderRequest {
    private String poNumber;
    private Long vendorId;
    private LocalDate orderDate;
    private String remarks;
    private BigDecimal totalAmount;
    private List<PurchaseOrderItemDTO> items;


    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<PurchaseOrderItemDTO> getItems() { return items; }
    public void setItems(List<PurchaseOrderItemDTO> items) { this.items = items; }


    public static class PurchaseOrderItemDTO {
        private String productName;
        private String purity;
        private BigDecimal weight;
        private BigDecimal ratePerUnit;
        private BigDecimal taxPercent;
        private BigDecimal totalPrice;



        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getPurity() { return purity; }
        public void setPurity(String purity) { this.purity = purity; }
        public BigDecimal getWeight() { return weight; }
        public void setWeight(BigDecimal weight) { this.weight = weight; }
        public BigDecimal getRatePerUnit() { return ratePerUnit; }
        public void setRatePerUnit(BigDecimal ratePerUnit) { this.ratePerUnit = ratePerUnit; }
        public BigDecimal getTaxPercent() { return taxPercent; }
        public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }
}