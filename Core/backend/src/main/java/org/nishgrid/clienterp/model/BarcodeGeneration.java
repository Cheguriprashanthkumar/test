package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "barcode_generation")
public class BarcodeGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "generation_id")
    private Long generationId;

    @Column(name = "barcode_value", unique = true, nullable = false, length = 50)
    private String barcodeValue;

    // Assuming you have an InventoryItem entity. If not, this can be a simple Long.
    @Column(name = "item_id")
    private Long itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "product_name", length = 100)
    private String productName;

    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(name = "purity", length = 10)
    private String purity;

    @Column(name = "hsn_code", length = 10)
    private String hsnCode;

    @Column(name = "purchase_rate", precision = 10, scale = 2)
    private BigDecimal purchaseRate;

    @Column(name = "making_charge", precision = 10, scale = 2)
    private BigDecimal makingCharge;

    @Column(name = "stone_charge", precision = 10, scale = 2)
    private BigDecimal stoneCharge;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_for", nullable = false)
    private CreatedFor createdFor;

    @Column(name = "linked_transaction_id")
    private Long linkedTransactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by")
    private Employee generatedBy;

    @Column(name = "generated_at", columnDefinition = "DATETIME")
    private LocalDateTime generatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "print_status")
    private PrintStatus printStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BarcodeStatus status;

    // Assuming you have a StoreLocation entity. If not, this can be a simple Long.
    @Column(name = "location_id")
    private Long locationId;

    @Lob
    @Column(name = "remarks")
    private String remarks;

    // Enum definitions (can be in separate files or nested)
    public enum ItemType {
        gold_jewellery, coin, diamond, silver, accessory
    }

    public enum CreatedFor {
        purchase, stock, repair, exchange
    }

    public enum PrintStatus {
        pending, printed
    }

    public enum BarcodeStatus {
        active, used, archived, damaged
    }

    // Lifecycle callback to set default values before persisting
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
        if (printStatus == null) {
            printStatus = PrintStatus.pending;
        }
        if (status == null) {
            status = BarcodeStatus.active;
        }
    }

    // Getters and Setters
    public Long getGenerationId() {
        return generationId;
    }

    public void setGenerationId(Long generationId) {
        this.generationId = generationId;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getPurity() {
        return purity;
    }

    public void setPurity(String purity) {
        this.purity = purity;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public BigDecimal getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(BigDecimal purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public BigDecimal getMakingCharge() {
        return makingCharge;
    }

    public void setMakingCharge(BigDecimal makingCharge) {
        this.makingCharge = makingCharge;
    }

    public BigDecimal getStoneCharge() {
        return stoneCharge;
    }

    public void setStoneCharge(BigDecimal stoneCharge) {
        this.stoneCharge = stoneCharge;
    }

    public CreatedFor getCreatedFor() {
        return createdFor;
    }

    public void setCreatedFor(CreatedFor createdFor) {
        this.createdFor = createdFor;
    }

    public Long getLinkedTransactionId() {
        return linkedTransactionId;
    }

    public void setLinkedTransactionId(Long linkedTransactionId) {
        this.linkedTransactionId = linkedTransactionId;
    }

    public Employee getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(Employee generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public PrintStatus getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(PrintStatus printStatus) {
        this.printStatus = printStatus;
    }

    public BarcodeStatus getStatus() {
        return status;
    }

    public void setStatus(BarcodeStatus status) {
        this.status = status;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}