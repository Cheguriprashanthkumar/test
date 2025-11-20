package org.nishgrid.clienterp.model;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "old_gold_exchange")
public class OldGoldExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String purchaseBillNo;

    private String sellerName;
    private String sellerMobile;
    private String sellerEmail;
    private String sellerAddress;
    private String sellerGstin;

    private LocalDate purchaseDate;
    private String payoutMode;
    private String otherPayoutMode;
    @Column(length = 500)
    private String remarks;

    // ADDED: Status field to track payment state (PENDING_PAYOUT, PAID, etc.)
    private String status;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPurchaseValue;
    @Column(precision = 5, scale = 2)
    private BigDecimal processingFeePercent;
    @Column(precision = 10, scale = 2)
    private BigDecimal processingFeeAmount;
    @Column(precision = 10, scale = 2)
    private BigDecimal netPayableAmount;

    @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OldGoldExchangeItem> items;

    public OldGoldExchange() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPurchaseBillNo() { return purchaseBillNo; }
    public void setPurchaseBillNo(String purchaseBillNo) { this.purchaseBillNo = purchaseBillNo; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public String getSellerMobile() { return sellerMobile; }
    public void setSellerMobile(String sellerMobile) { this.sellerMobile = sellerMobile; }
    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }
    public String getSellerAddress() { return sellerAddress; }
    public void setSellerAddress(String sellerAddress) { this.sellerAddress = sellerAddress; }
    public String getSellerGstin() { return sellerGstin; }
    public void setSellerGstin(String sellerGstin) { this.sellerGstin = sellerGstin; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getPayoutMode() { return payoutMode; }
    public void setPayoutMode(String payoutMode) { this.payoutMode = payoutMode; }
    public String getOtherPayoutMode() { return otherPayoutMode; }
    public void setOtherPayoutMode(String otherPayoutMode) { this.otherPayoutMode = otherPayoutMode; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    // UPDATED: Getter for the new status field
    public String getStatus() { return status; }
    // ADDED: Setter for the new status field
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalPurchaseValue() { return totalPurchaseValue; }
    public void setTotalPurchaseValue(BigDecimal totalPurchaseValue) { this.totalPurchaseValue = totalPurchaseValue; }
    public BigDecimal getProcessingFeePercent() { return processingFeePercent; }
    public void setProcessingFeePercent(BigDecimal processingFeePercent) { this.processingFeePercent = processingFeePercent; }
    public BigDecimal getProcessingFeeAmount() { return processingFeeAmount; }
    public void setProcessingFeeAmount(BigDecimal processingFeeAmount) { this.processingFeeAmount = processingFeeAmount; }
    public BigDecimal getNetPayableAmount() { return netPayableAmount; }
    public void setNetPayableAmount(BigDecimal netPayableAmount) { this.netPayableAmount = netPayableAmount; }
    public List<OldGoldExchangeItem> getItems() { return items; }
    public void setItems(List<OldGoldExchangeItem> items) { this.items = items; }
}