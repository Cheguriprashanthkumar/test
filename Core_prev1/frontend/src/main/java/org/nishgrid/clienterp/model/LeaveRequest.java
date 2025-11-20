package org.nishgrid.clienterp.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class LeaveRequest {

    private IntegerProperty leaveId = new SimpleIntegerProperty();
    private ObjectProperty<LocalDate> appliedOn = new SimpleObjectProperty<>();
    private StringProperty approvedBy = new SimpleStringProperty();
    private ObjectProperty<LocalDate> fromDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> toDate = new SimpleObjectProperty<>();
    private StringProperty holidayName = new SimpleStringProperty();
    private StringProperty leavePaymentType = new SimpleStringProperty();
    private StringProperty leaveType = new SimpleStringProperty();
    private StringProperty status = new SimpleStringProperty();
    private IntegerProperty totalDays = new SimpleIntegerProperty();

    // Nested employee
    private Employee employee;

    public LeaveRequest() {}

    // --- Properties (for TableView bindings) ---
    public IntegerProperty leaveIdProperty() { return leaveId; }
    public ObjectProperty<LocalDate> appliedOnProperty() { return appliedOn; }
    public StringProperty approvedByProperty() { return approvedBy; }
    public ObjectProperty<LocalDate> fromDateProperty() { return fromDate; }
    public ObjectProperty<LocalDate> toDateProperty() { return toDate; }
    public StringProperty holidayNameProperty() { return holidayName; }
    public StringProperty leavePaymentTypeProperty() { return leavePaymentType; }
    public StringProperty leaveTypeProperty() { return leaveType; }
    public StringProperty statusProperty() { return status; }
    public IntegerProperty totalDaysProperty() { return totalDays; }

    // --- Getters & Setters ---
    public int getLeaveId() { return leaveId.get(); }
    public void setLeaveId(int leaveId) { this.leaveId.set(leaveId); }

    public LocalDate getAppliedOn() { return appliedOn.get(); }
    public void setAppliedOn(LocalDate appliedOn) { this.appliedOn.set(appliedOn); }

    public String getApprovedBy() { return approvedBy.get(); }
    public void setApprovedBy(String approvedBy) { this.approvedBy.set(approvedBy); }

    public LocalDate getFromDate() { return fromDate.get(); }
    public void setFromDate(LocalDate fromDate) { this.fromDate.set(fromDate); }

    public LocalDate getToDate() { return toDate.get(); }
    public void setToDate(LocalDate toDate) { this.toDate.set(toDate); }

    public String getHolidayName() { return holidayName.get(); }
    public void setHolidayName(String holidayName) { this.holidayName.set(holidayName); }

    public String getLeavePaymentType() { return leavePaymentType.get(); }
    public void setLeavePaymentType(String leavePaymentType) { this.leavePaymentType.set(leavePaymentType); }

    public String getLeaveType() { return leaveType.get(); }
    public void setLeaveType(String leaveType) { this.leaveType.set(leaveType); }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    public int getTotalDays() { return totalDays.get(); }
    public void setTotalDays(int totalDays) { this.totalDays.set(totalDays); }

    // --- Employee (nested) ---
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getEmployeeCode() {
        return employee != null ? employee.getEmployeeCode() : "";
    }

    public void setEmployeeCode(String code) {
        if (this.employee == null) {
            this.employee = new Employee();
        }
        this.employee.setEmployeeCode(code);
    }

    public String getFullName() {
        return employee != null ? employee.getFullName() : "";
    }

    public void setFullName(String name) {
        if (this.employee == null) {
            this.employee = new Employee();
        }
        this.employee.setFullName(name);
    }
}
