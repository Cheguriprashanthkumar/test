package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayslipFx {
    private int payslipId;
    private String month;
    private int totalWorkingDays;
    private int daysPresent;
    private BigDecimal totalSalary;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
    private String paymentStatus;
    // --- CHANGE THIS LINE ---
    private EmployeeFx employee; // Changed from Employee to EmployeeFx

    // Getters and Setters
    public int getPayslipId() { return payslipId; }
    public void setPayslipId(int payslipId) { this.payslipId = payslipId; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getTotalWorkingDays() { return totalWorkingDays; }
    public void setTotalWorkingDays(int totalWorkingDays) { this.totalWorkingDays = totalWorkingDays; }

    public int getDaysPresent() { return daysPresent; }
    public void setDaysPresent(int daysPresent) { this.daysPresent = daysPresent; }

    public BigDecimal getTotalSalary() { return totalSalary; }
    public void setTotalSalary(BigDecimal totalSalary) { this.totalSalary = totalSalary; }

    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    // --- AND CHANGE THE GETTER/SETTER ---
    public EmployeeFx getEmployee() { return employee; }
    public void setEmployee(EmployeeFx employee) { this.employee = employee; }

    public String getEmployeeCode() {
        return employee != null ? employee.getEmployeeCode() : "";
    }
}