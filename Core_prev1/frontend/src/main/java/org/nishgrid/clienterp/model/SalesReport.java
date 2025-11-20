package org.nishgrid.clienterp.model;

import javafx.beans.property.*;
import java.math.BigDecimal;

public class SalesReport {
    private final LongProperty reportId;
    private final StringProperty reportDate;
    private final StringProperty reportType;
    private final ObjectProperty<BigDecimal> totalSales;
    private final ObjectProperty<BigDecimal> totalDiscount;
    private final IntegerProperty totalItemsSold;
    private final IntegerProperty totalCustomers;
    private final StringProperty generatedOn;

    public SalesReport(long reportId, String reportDate, String reportType, BigDecimal totalSales,
            /* Removed BigDecimal totalTax, */ BigDecimal totalDiscount, int totalItemsSold,
                       int totalCustomers, String generatedOn) {
        this.reportId = new SimpleLongProperty(reportId);
        this.reportDate = new SimpleStringProperty(reportDate);
        this.reportType = new SimpleStringProperty(reportType);
        this.totalSales = new SimpleObjectProperty<>(totalSales);
        // Removed this.totalTax = new SimpleObjectProperty<>(totalTax);
        this.totalDiscount = new SimpleObjectProperty<>(totalDiscount);
        this.totalItemsSold = new SimpleIntegerProperty(totalItemsSold);
        this.totalCustomers = new SimpleIntegerProperty(totalCustomers);
        this.generatedOn = new SimpleStringProperty(generatedOn);
    }

    public long getReportId() { return reportId.get(); }
    public LongProperty reportIdProperty() { return reportId; }
    public StringProperty reportDateProperty() { return reportDate; }
    public StringProperty reportTypeProperty() { return reportType; }
    public ObjectProperty<BigDecimal> totalSalesProperty() { return totalSales; }
    // Removed public ObjectProperty<BigDecimal> totalTaxProperty() { return totalTax; }
    public ObjectProperty<BigDecimal> totalDiscountProperty() { return totalDiscount; }
    public IntegerProperty totalItemsSoldProperty() { return totalItemsSold; }
    public IntegerProperty totalCustomersProperty() { return totalCustomers; }
    public StringProperty generatedOnProperty() { return generatedOn; }
}