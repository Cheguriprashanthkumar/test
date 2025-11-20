package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReturnRequestDTO {

    private Long invoiceId;
    private Long salesItemId;
    private LocalDate returnDate;
    private String returnReason;
    private Integer quantity;
    private BigDecimal returnAmount;
    private String refundMode;
    private String handledBy;

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public Long getSalesItemId() { return salesItemId; }
    public void setSalesItemId(Long salesItemId) { this.salesItemId = salesItemId; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getReturnAmount() { return returnAmount; }
    public void setReturnAmount(BigDecimal returnAmount) { this.returnAmount = returnAmount; }

    public String getRefundMode() { return refundMode; }
    public void setRefundMode(String refundMode) { this.refundMode = refundMode; }

    public String getHandledBy() { return handledBy; }
    public void setHandledBy(String handledBy) { this.handledBy = handledBy; }
}