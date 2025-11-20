package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payslips")
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payslipId;

    @NotNull(message = "Employee is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode")
    private Employee employee;

    @Column(name = "month_name")
    private String month;

    private Integer totalWorkingDays;
    private Integer daysPresent;
    private BigDecimal totalSalary;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
    private LocalDate generatedOn;
    private String paymentStatus;

    // Standard Getters and Setters
    public Integer getPayslipId() {
        return payslipId;
    }

    public void setPayslipId(Integer payslipId) {
        this.payslipId = payslipId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getTotalWorkingDays() {
        return totalWorkingDays;
    }

    public void setTotalWorkingDays(Integer totalWorkingDays) {
        this.totalWorkingDays = totalWorkingDays;
    }

    public Integer getDaysPresent() {
        return daysPresent;
    }

    public void setDaysPresent(Integer daysPresent) {
        this.daysPresent = daysPresent;
    }

    public BigDecimal getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(BigDecimal totalSalary) {
        this.totalSalary = totalSalary;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(BigDecimal totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public LocalDate getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(LocalDate generatedOn) {
        this.generatedOn = generatedOn;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}