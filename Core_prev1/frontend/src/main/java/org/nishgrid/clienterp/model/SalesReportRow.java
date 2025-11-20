package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class SalesReportRow {
    private final ObjectProperty<LocalDate> date;
    private final IntegerProperty totalInvoices;
    private final DoubleProperty totalAmount;
    private final DoubleProperty netAmount;

    public SalesReportRow(LocalDate date, int totalInvoices, double totalAmount, double netAmount) {
        this.date = new SimpleObjectProperty<>(date);
        this.totalInvoices = new SimpleIntegerProperty(totalInvoices);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.netAmount = new SimpleDoubleProperty(netAmount);
    }

    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public IntegerProperty totalInvoicesProperty() { return totalInvoices; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public DoubleProperty netAmountProperty() { return netAmount; }

    public LocalDate getDate() { return date.get(); }
    public int getTotalInvoices() { return totalInvoices.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    public double getNetAmount() { return netAmount.get(); }
}
