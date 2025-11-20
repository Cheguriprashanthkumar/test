package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceLogFx {

    private int logId;

    @JsonProperty("date")
    private LocalDate attendanceDate;

    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private BigDecimal workingHours;
    private String presentStatus;

    // --- CHANGED HERE ---
    private EmployeeFx employee;

    // Helper to access nested employeeCode (no changes needed here)
    public String getEmployeeCode() {
        return employee != null ? employee.getEmployeeCode() : "";
    }

    // Helper to access the nested employee name (no changes needed here)
    public String getEmployeeName() {
        return employee != null ? employee.getFullName() : "";
    }

    // Getters and Setters for AttendanceLogFx properties
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public BigDecimal getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(BigDecimal workingHours) {
        this.workingHours = workingHours;
    }

    public String getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(String presentStatus) {
        this.presentStatus = presentStatus;
    }

    // --- CHANGED GETTER AND SETTER FOR EMPLOYEE ---
    public EmployeeFx getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeFx employee) {
        this.employee = employee;
    }

    // --- CHANGED HELPER METHOD ---
    public void setEmployeeCode(String employeeCode) {
        if (this.employee == null) {
            // Instantiate the correct EmployeeFx class
            this.employee = new EmployeeFx();
        }
        this.employee.setEmployeeCode(employeeCode);
    }
}