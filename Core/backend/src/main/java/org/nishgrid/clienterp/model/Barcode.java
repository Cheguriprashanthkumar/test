package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "barcode_tracking")
@Getter
@Setter
public class Barcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grn_id", nullable = false)
    private GoodsReceiptNote grn;

    @Column(nullable = false)
    private String itemName;

    @Column(unique = true, nullable = false)
    private String barcodeNo;

    private BigDecimal weight;

    private String scannedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GoodsReceiptNote getGrn() {
        return grn;
    }

    public void setGrn(GoodsReceiptNote grn) {
        this.grn = grn;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBarcodeNo() {
        return barcodeNo;
    }

    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getScannedBy() {
        return scannedBy;
    }

    public void setScannedBy(String scannedBy) {
        this.scannedBy = scannedBy;
    }
}