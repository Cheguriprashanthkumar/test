package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_logs")
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @NotNull(message = "Employee is required")
    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode")
    private Employee employee;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    @DecimalMin(value = "0.0", inclusive = true, message = "Working hours cannot be negative")
    private BigDecimal workingHours;

    @NotBlank(message = "Present status is required")
    private String presentStatus;



    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}
