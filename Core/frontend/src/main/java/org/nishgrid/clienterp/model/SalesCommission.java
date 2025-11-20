package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class SalesCommission {
    private final LongProperty commissionId = new SimpleLongProperty();
    private final StringProperty employeeName = new SimpleStringProperty();
    private final StringProperty invoiceNo = new SimpleStringProperty();
    private final StringProperty commissionRate = new SimpleStringProperty();
    private final DoubleProperty amountTotal = new SimpleDoubleProperty();
    private int employeeId; // Store raw IDs for updates
    private int invoiceId;

    public SalesCommission(long id, String empName, int empId, String invNo, int invId, String rate, double amount) {
        this.commissionId.set(id);
        this.employeeName.set(empName);
        this.employeeId = empId;
        this.invoiceNo.set(invNo);
        this.invoiceId = invId;
        this.commissionRate.set(rate);
        this.amountTotal.set(amount);
    }
    // Getters for IDs
    public long getCommissionId() { return commissionId.get(); }
    public int getEmployeeId() { return employeeId; }
    public int getInvoiceId() { return invoiceId; }
    // Property getters for table display
    public LongProperty commissionIdProperty() { return commissionId; }
    public StringProperty employeeNameProperty() { return employeeName; }
    public StringProperty invoiceNoProperty() { return invoiceNo; }
    public StringProperty commissionRateProperty() { return commissionRate; }
    public DoubleProperty amountTotalProperty() { return amountTotal; }
}