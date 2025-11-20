package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
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

    // ADDED: Setter for the new status field
    // UPDATED: Getter for the new status field
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

}