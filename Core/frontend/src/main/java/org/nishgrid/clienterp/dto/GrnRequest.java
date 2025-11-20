package org.nishgrid.clienterp.dto;

import java.time.LocalDate;

public class GrnRequest {
    private String grnNumber;
    private Long purchaseOrderId;
    private LocalDate receivedDate;
    private String receivedBy;
    private String remarks;


    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }
    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }
    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}