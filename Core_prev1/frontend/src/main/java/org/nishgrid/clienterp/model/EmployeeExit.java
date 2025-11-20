package org.nishgrid.clienterp.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class EmployeeExit {
    private final IntegerProperty exitId = new SimpleIntegerProperty();
    private final StringProperty employeeCode = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> exitDate = new SimpleObjectProperty<>();
    private final StringProperty exitReason = new SimpleStringProperty();
    private final DoubleProperty finalSettlement = new SimpleDoubleProperty();
    private final StringProperty feedbackNotes = new SimpleStringProperty();
    private final StringProperty clearanceStatus = new SimpleStringProperty();

    // This field is correct
    private EmployeeFx employee;

    public int getExitId() { return exitId.get(); }
    public void setExitId(int id) { this.exitId.set(id); }
    public IntegerProperty exitIdProperty() { return exitId; }

    public String getEmployeeCode() { return employeeCode.get(); }
    public void setEmployeeCode(String code) { this.employeeCode.set(code); }
    public StringProperty employeeCodeProperty() { return employeeCode; }

    public LocalDate getExitDate() { return exitDate.get(); }
    public void setExitDate(LocalDate date) { this.exitDate.set(date); }
    public ObjectProperty<LocalDate> exitDateProperty() { return exitDate; }

    public String getExitReason() { return exitReason.get(); }
    public void setExitReason(String reason) { this.exitReason.set(reason); }
    public StringProperty exitReasonProperty() { return exitReason; }

    public double getFinalSettlement() { return finalSettlement.get(); }
    public void setFinalSettlement(double amount) { this.finalSettlement.set(amount); }
    public DoubleProperty finalSettlementProperty() { return finalSettlement; }

    public String getFeedbackNotes() { return feedbackNotes.get(); }
    public void setFeedbackNotes(String notes) { this.feedbackNotes.set(notes); }
    public StringProperty feedbackNotesProperty() { return feedbackNotes; }

    public String getClearanceStatus() { return clearanceStatus.get(); }
    public void setClearanceStatus(String status) { this.clearanceStatus.set(status); }
    public StringProperty clearanceStatusProperty() { return clearanceStatus; }

    // ✅ FIXED: Changed return type from 'Employee' to 'EmployeeFx'
    public EmployeeFx getEmployee() {
        return employee;
    }

    // ✅ FIXED: Changed parameter type from 'Employee' to 'EmployeeFx'
    public void setEmployee(EmployeeFx employee) {
        this.employee = employee;
    }

    // Aliases (optional)
    public int getId() { return getExitId(); }
    public IntegerProperty idProperty() { return exitId; }
    public StringProperty reasonProperty() { return exitReason; }
    public DoubleProperty settlementProperty() { return finalSettlement; }
    public StringProperty feedbackProperty() { return feedbackNotes; }
}