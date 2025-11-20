package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class Gstr3bSummary {
    private final StringProperty monthYear;
    private final DoubleProperty outwardSupplies;
    private final DoubleProperty inwardSupplies;
    private final DoubleProperty igst;
    private final DoubleProperty cgst;
    private final DoubleProperty sgst;
    private final StringProperty filedStatus;
    private final StringProperty filedDate;

    public Gstr3bSummary(String monthYear, double outward, double inward,
                         double igst, double cgst, double sgst, String status, String date) {
        this.monthYear = new SimpleStringProperty(monthYear);
        this.outwardSupplies = new SimpleDoubleProperty(outward);
        this.inwardSupplies = new SimpleDoubleProperty(inward);
        this.igst = new SimpleDoubleProperty(igst);
        this.cgst = new SimpleDoubleProperty(cgst);
        this.sgst = new SimpleDoubleProperty(sgst);
        this.filedStatus = new SimpleStringProperty(status);
        this.filedDate = new SimpleStringProperty(date);
    }

    public StringProperty monthYearProperty() { return monthYear; }
    public DoubleProperty outwardSuppliesProperty() { return outwardSupplies; }
    public DoubleProperty inwardSuppliesProperty() { return inwardSupplies; }
    public DoubleProperty igstProperty() { return igst; }
    public DoubleProperty cgstProperty() { return cgst; }
    public DoubleProperty sgstProperty() { return sgst; }
    public StringProperty filedStatusProperty() { return filedStatus; }
    public StringProperty filedDateProperty() { return filedDate; }
}
