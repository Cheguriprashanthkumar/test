package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class GoodsReceiptNote {
    private final long id;
    private final String grnNumber;
    private final PurchaseOrder purchaseOrder;
    private final LocalDate receivedDate;
    private final String receivedBy;
    private final String remarks;

    @JsonCreator
    public GoodsReceiptNote(@JsonProperty("id") long id,
                            @JsonProperty("grnNumber") String grnNumber,
                            @JsonProperty("purchaseOrder") PurchaseOrder purchaseOrder,
                            @JsonProperty("receivedDate") LocalDate receivedDate,
                            @JsonProperty("receivedBy") String receivedBy,
                            @JsonProperty("remarks") String remarks) {
        this.id = id;
        this.grnNumber = grnNumber;
        this.purchaseOrder = purchaseOrder;
        this.receivedDate = receivedDate;
        this.receivedBy = receivedBy;
        this.remarks = remarks;
    }

    public long getId() { return id; }
    public String getGrnNumber() { return grnNumber; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public String getReceivedBy() { return receivedBy; }
    public String getRemarks() { return remarks; }
}