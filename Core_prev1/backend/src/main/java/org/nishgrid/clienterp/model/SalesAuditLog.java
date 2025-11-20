package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_audit_logs")
public class SalesAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    private String action;
    private String performedBy;
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String oldData;
    @Column(columnDefinition = "TEXT")
    private String newData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private SalesInvoice salesInvoice;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public SalesInvoice getSalesInvoice() {
        return salesInvoice;
    }

    public void setSalesInvoice(SalesInvoice salesInvoice) {
        this.salesInvoice = salesInvoice;
    }
}