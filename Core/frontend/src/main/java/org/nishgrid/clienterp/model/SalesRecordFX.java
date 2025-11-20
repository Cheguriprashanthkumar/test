package org.nishgrid.clienterp.model;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesRecordFX {

    private final SimpleLongProperty id;
    private final SimpleStringProperty invoiceNo;
    private final SimpleObjectProperty<LocalDate> invoiceDate;
    private final SimpleStringProperty customerName;
    private final SimpleObjectProperty<BigDecimal> finalAmount;
    private final SimpleObjectProperty<BigDecimal> oldGoldValue;
    private final SimpleObjectProperty<BigDecimal> dueAmount;


    public SalesRecordFX(Long id, String invoiceNo, LocalDate invoiceDate, String customerName, BigDecimal finalAmount) {
        this.id = new SimpleLongProperty(id);
        this.invoiceNo = new SimpleStringProperty(invoiceNo);
        this.invoiceDate = new SimpleObjectProperty<>(invoiceDate);
        this.customerName = new SimpleStringProperty(customerName);
        this.finalAmount = new SimpleObjectProperty<>(finalAmount);
        this.oldGoldValue = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.dueAmount = new SimpleObjectProperty<>(BigDecimal.ZERO);
    }

    public long getId() {
        return id.get();
    }

    public SimpleLongProperty idProperty() {
        return id;
    }

    public String getInvoiceNo() {
        return invoiceNo.get();
    }

    public SimpleStringProperty invoiceNoProperty() {
        return invoiceNo;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate.get();
    }

    public SimpleObjectProperty<LocalDate> invoiceDateProperty() {
        return invoiceDate;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount.get();
    }

    public SimpleObjectProperty<BigDecimal> finalAmountProperty() {
        return finalAmount;
    }

    // New getters and setters for oldGoldValue
    public BigDecimal getOldGoldValue() {
        return oldGoldValue.get();
    }

    public SimpleObjectProperty<BigDecimal> oldGoldValueProperty() {
        return oldGoldValue;
    }

    public void setOldGoldValue(BigDecimal oldGoldValue) {
        this.oldGoldValue.set(oldGoldValue);
    }

    // New getters and setters for dueAmount
    public BigDecimal getDueAmount() {
        return dueAmount.get();
    }

    public SimpleObjectProperty<BigDecimal> dueAmountProperty() {
        return dueAmount;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount.set(dueAmount);
    }
}