package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class PurchaseOrderItem {
    private final long id;
    private final String productName;
    private final String purity;
    private final BigDecimal weight;
    private final BigDecimal ratePerUnit;
    private final BigDecimal taxPercent;
    private final BigDecimal totalPrice;

    @JsonCreator
    public PurchaseOrderItem(@JsonProperty("id") long id,
                             @JsonProperty("productName") String productName,
                             @JsonProperty("purity") String purity,
                             @JsonProperty("weight") BigDecimal weight,
                             @JsonProperty("ratePerUnit") BigDecimal ratePerUnit,
                             @JsonProperty("taxPercent") BigDecimal taxPercent,
                             @JsonProperty("totalPrice") BigDecimal totalPrice) {
        this.id = id;
        this.productName = productName;
        this.purity = purity;
        this.weight = weight;
        this.ratePerUnit = ratePerUnit;
        this.taxPercent = taxPercent;
        this.totalPrice = totalPrice;
    }

    public long getId() { return id; }
    public String getProductName() { return productName; }
    public String getPurity() { return purity; }
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getRatePerUnit() { return ratePerUnit; }
    public BigDecimal getTaxPercent() { return taxPercent; }
    public BigDecimal getTotalPrice() { return totalPrice; }
}