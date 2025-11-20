package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "old_gold_exchange_item")
public class OldGoldExchangeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String metalType;
    private String purity;
    private Double grossWeight;
    private Double netWeight;

    @Column(precision = 5, scale = 2)
    private BigDecimal wastagePercent;
    @Column(precision = 10, scale = 2)
    private BigDecimal ratePerGram;
    @Column(precision = 5, scale = 2)
    private BigDecimal diamondCarat;
    @Column(precision = 10, scale = 2)
    private BigDecimal diamondRate;
    @Column(precision = 10, scale = 2)
    private BigDecimal deductionCharge;
    @Column(precision = 10, scale = 2)
    private BigDecimal totalItemValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    @JsonIgnore
    private OldGoldExchange exchange;

    public OldGoldExchangeItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getMetalType() { return metalType; }
    public void setMetalType(String metalType) { this.metalType = metalType; }
    public String getPurity() { return purity; }
    public void setPurity(String purity) { this.purity = purity; }
    public Double getGrossWeight() { return grossWeight; }
    public void setGrossWeight(Double grossWeight) { this.grossWeight = grossWeight; }
    public Double getNetWeight() { return netWeight; }
    public void setNetWeight(Double netWeight) { this.netWeight = netWeight; }
    public BigDecimal getWastagePercent() { return wastagePercent; }
    public void setWastagePercent(BigDecimal wastagePercent) { this.wastagePercent = wastagePercent; }
    public BigDecimal getRatePerGram() { return ratePerGram; }
    public void setRatePerGram(BigDecimal ratePerGram) { this.ratePerGram = ratePerGram; }
    public BigDecimal getDiamondCarat() { return diamondCarat; }
    public void setDiamondCarat(BigDecimal diamondCarat) { this.diamondCarat = diamondCarat; }
    public BigDecimal getDiamondRate() { return diamondRate; }
    public void setDiamondRate(BigDecimal diamondRate) { this.diamondRate = diamondRate; }
    public BigDecimal getDeductionCharge() { return deductionCharge; }
    public void setDeductionCharge(BigDecimal deductionCharge) { this.deductionCharge = deductionCharge; }
    public BigDecimal getTotalItemValue() { return totalItemValue; }
    public void setTotalItemValue(BigDecimal totalItemValue) { this.totalItemValue = totalItemValue; }
    public OldGoldExchange getExchange() { return exchange; }
    public void setExchange(OldGoldExchange exchange) { this.exchange = exchange; }
}