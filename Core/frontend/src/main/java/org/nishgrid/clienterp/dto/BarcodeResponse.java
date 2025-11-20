package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;

public class BarcodeResponse {
    private Long id;
    private Long grnId;
    private String grnNumber;
    private String itemName;
    private String barcodeNo;
    private BigDecimal weight;
    private String scannedBy;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGrnId() { return grnId; }
    public void setGrnId(Long grnId) { this.grnId = grnId; }
    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getBarcodeNo() { return barcodeNo; }
    public void setBarcodeNo(String barcodeNo) { this.barcodeNo = barcodeNo; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getScannedBy() { return scannedBy; }
    public void setScannedBy(String scannedBy) { this.scannedBy = scannedBy; }
}