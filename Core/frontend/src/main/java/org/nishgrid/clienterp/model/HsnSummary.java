package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class HsnSummary {
    private final StringProperty hsnCode;
    private final DoubleProperty totalQty;
    private final DoubleProperty taxableValue;
    private final DoubleProperty gstAmount;
    private final StringProperty month;

    public HsnSummary(String hsnCode, double totalQty, double taxableValue, double gstAmount, String month) {
        this.hsnCode = new SimpleStringProperty(hsnCode);
        this.totalQty = new SimpleDoubleProperty(totalQty);
        this.taxableValue = new SimpleDoubleProperty(taxableValue);
        this.gstAmount = new SimpleDoubleProperty(gstAmount);
        this.month = new SimpleStringProperty(month);
    }

    public StringProperty hsnCodeProperty() { return hsnCode; }
    public DoubleProperty totalQtyProperty() { return totalQty; }
    public DoubleProperty taxableValueProperty() { return taxableValue; }
    public DoubleProperty gstAmountProperty() { return gstAmount; }
    public StringProperty monthProperty() { return month; }
}