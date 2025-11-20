package org.nishgrid.clienterp.model;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceRow {
    private final LongProperty id;
    private final StringProperty invoiceNo;
    private final StringProperty customerName;
    private final ObjectProperty<LocalDate> invoiceDate;
    private final StringProperty salesType;
    private final StringProperty paymentMode;
    private final DoubleProperty totalAmount;
    private final DoubleProperty netAmount;
    private final DoubleProperty paidAmount;
    private final DoubleProperty dueAmount;
    private final StringProperty status;
    private final DoubleProperty oldGoldValue;

    public InvoiceRow(Long id, String invoiceNo, String customerName, LocalDate invoiceDate, String salesType,
                      String paymentMode, double totalAmount, double netAmount, double paidAmount, double dueAmount, String status, double oldGoldValue) {
        this.id = new SimpleLongProperty(id);
        this.invoiceNo = new SimpleStringProperty(invoiceNo);
        this.customerName = new SimpleStringProperty(customerName);
        this.invoiceDate = new SimpleObjectProperty<>(invoiceDate);
        this.salesType = new SimpleStringProperty(salesType);
        this.paymentMode = new SimpleStringProperty(paymentMode);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.netAmount = new SimpleDoubleProperty(new BigDecimal(netAmount).subtract(new BigDecimal(oldGoldValue)).doubleValue());
        this.paidAmount = new SimpleDoubleProperty(paidAmount);
        this.dueAmount = new SimpleDoubleProperty(new BigDecimal(netAmount).subtract(new BigDecimal(oldGoldValue)).subtract(new BigDecimal(paidAmount)).doubleValue());
        this.status = new SimpleStringProperty(status);
        this.oldGoldValue = new SimpleDoubleProperty(oldGoldValue);
    }

    public long getId() { return id.get(); }
    public String getInvoiceNo() { return invoiceNo.get(); }
    public String getStatus() { return status.get(); }
    public double getDueAmount() { return dueAmount.get(); }
    public double getOldGoldValue() { return oldGoldValue.get(); }
    public double getNetAmount() { return netAmount.get(); }

    public void setPaidAmount(double value) { this.paidAmount.set(value); }
    public void setDueAmount(double value) { this.dueAmount.set(value); }
    public void setStatus(String value) { this.status.set(value); }

    public LongProperty idProperty() { return id; }
    public StringProperty invoiceNoProperty() { return invoiceNo; }
    public StringProperty customerNameProperty() { return customerName; }
    public ObjectProperty<LocalDate> invoiceDateProperty() { return invoiceDate; }
    public StringProperty salesTypeProperty() { return salesType; }
    public StringProperty paymentModeProperty() { return paymentMode; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public DoubleProperty netAmountProperty() { return netAmount; }
    public DoubleProperty paidAmountProperty() { return paidAmount; }
    public DoubleProperty dueAmountProperty() { return dueAmount; }
    public StringProperty statusProperty() { return status; }
    public DoubleProperty oldGoldValueProperty() { return oldGoldValue; }
}