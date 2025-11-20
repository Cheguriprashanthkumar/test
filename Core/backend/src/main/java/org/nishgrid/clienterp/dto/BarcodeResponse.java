package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.Barcode;
import java.math.BigDecimal;

@Data
public class BarcodeResponse {
    private Long id;
    private Long grnId;
    private String grnNumber;
    private String itemName;
    private String barcodeNo;
    private BigDecimal weight;
    private String scannedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrnId() {
        return grnId;
    }

    public void setGrnId(Long grnId) {
        this.grnId = grnId;
    }

    public String getGrnNumber() {
        return grnNumber;
    }

    public void setGrnNumber(String grnNumber) {
        this.grnNumber = grnNumber;
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

    public static BarcodeResponse fromEntity(Barcode barcode) {
        BarcodeResponse dto = new BarcodeResponse();
        dto.setId(barcode.getId());
        dto.setGrnId(barcode.getGrn().getId());
        dto.setGrnNumber(barcode.getGrn().getGrnNumber());
        dto.setItemName(barcode.getItemName());
        dto.setBarcodeNo(barcode.getBarcodeNo());
        dto.setWeight(barcode.getWeight());
        dto.setScannedBy(barcode.getScannedBy());
        return dto;
    }
}