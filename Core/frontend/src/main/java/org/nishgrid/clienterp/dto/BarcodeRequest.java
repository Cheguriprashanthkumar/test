package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.util.List;

public class BarcodeRequest {
    private Long grnId;
    private String scannedBy;
    private List<BarcodeItemDTO> barcodes;


    public Long getGrnId() { return grnId; }
    public void setGrnId(Long grnId) { this.grnId = grnId; }
    public String getScannedBy() { return scannedBy; }
    public void setScannedBy(String scannedBy) { this.scannedBy = scannedBy; }
    public List<BarcodeItemDTO> getBarcodes() { return barcodes; }
    public void setBarcodes(List<BarcodeItemDTO> barcodes) { this.barcodes = barcodes; }

    public static class BarcodeItemDTO {
        private String itemName;
        private String barcodeNo;
        private BigDecimal weight;


        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public String getBarcodeNo() { return barcodeNo; }
        public void setBarcodeNo(String barcodeNo) { this.barcodeNo = barcodeNo; }
        public BigDecimal getWeight() { return weight; }
        public void setWeight(BigDecimal weight) { this.weight = weight; }
    }
}