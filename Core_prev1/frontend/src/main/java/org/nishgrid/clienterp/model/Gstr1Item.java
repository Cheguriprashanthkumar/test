package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class Gstr1Item {
    private final IntegerProperty invoiceId;
    private final StringProperty customerGstin;
    private final StringProperty invoiceDate;
    private final StringProperty itemHsn;
    private final DoubleProperty taxableValue;
    private final DoubleProperty gstRate;
    private final DoubleProperty cgstAmount;
    private final DoubleProperty sgstAmount;
    private final DoubleProperty igstAmount;
    private final StringProperty exportMonth;

    public Gstr1Item(int invoiceId, String gstin, String date, String hsn, double taxable, double rate,
                     double cgst, double sgst, double igst, String month) {
        this.invoiceId = new SimpleIntegerProperty(invoiceId);
        this.customerGstin = new SimpleStringProperty(gstin);
        this.invoiceDate = new SimpleStringProperty(date);
        this.itemHsn = new SimpleStringProperty(hsn);
        this.taxableValue = new SimpleDoubleProperty(taxable);
        this.gstRate = new SimpleDoubleProperty(rate);
        this.cgstAmount = new SimpleDoubleProperty(cgst);
        this.sgstAmount = new SimpleDoubleProperty(sgst);
        this.igstAmount = new SimpleDoubleProperty(igst);
        this.exportMonth = new SimpleStringProperty(month);
    }

    public IntegerProperty invoiceIdProperty() { return invoiceId; }
    public StringProperty customerGstinProperty() { return customerGstin; }
    public StringProperty invoiceDateProperty() { return invoiceDate; }
    public StringProperty itemHsnProperty() { return itemHsn; }
    public DoubleProperty taxableValueProperty() { return taxableValue; }
    public DoubleProperty gstRateProperty() { return gstRate; }
    public DoubleProperty cgstAmountProperty() { return cgstAmount; }
    public DoubleProperty sgstAmountProperty() { return sgstAmount; }
    public DoubleProperty igstAmountProperty() { return igstAmount; }
    public StringProperty exportMonthProperty() { return exportMonth; }
}
