package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(name = "salary_structure")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Employee is required")
    @OneToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode")
    private Employee employee;

    @DecimalMin(value = "0.0", inclusive = true, message = "Basic salary cannot be negative")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "HRA cannot be negative")
    private BigDecimal hra;

    @DecimalMin(value = "0.0", inclusive = true, message = "Other allowances cannot be negative")
    private BigDecimal otherAllowances;

    @DecimalMin(value = "0.0", inclusive = true, message = "PF deduction cannot be negative")
    private BigDecimal pfDeduction;

    @DecimalMin(value = "0.0", inclusive = true, message = "ESI deduction cannot be negative")
    private BigDecimal esiDeduction;

    @DecimalMin(value = "0.0", inclusive = true, message = "TDS deduction cannot be negative")
    private BigDecimal tdsDeduction;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total salary cannot be negative")
    private BigDecimal totalSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "Year salary cannot be negative")
    private BigDecimal yearSalary;

    // Getters & Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getHra() {
        return hra;
    }

    public void setHra(BigDecimal hra) {
        this.hra = hra;
    }

    public BigDecimal getOtherAllowances() {
        return otherAllowances;
    }

    public void setOtherAllowances(BigDecimal otherAllowances) {
        this.otherAllowances = otherAllowances;
    }

    public BigDecimal getPfDeduction() {
        return pfDeduction;
    }

    public void setPfDeduction(BigDecimal pfDeduction) {
        this.pfDeduction = pfDeduction;
    }

    public BigDecimal getEsiDeduction() {
        return esiDeduction;
    }

    public void setEsiDeduction(BigDecimal esiDeduction) {
        this.esiDeduction = esiDeduction;
    }

    public BigDecimal getTdsDeduction() {
        return tdsDeduction;
    }

    public void setTdsDeduction(BigDecimal tdsDeduction) {
        this.tdsDeduction = tdsDeduction;
    }

    public BigDecimal getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(BigDecimal totalSalary) {
        this.totalSalary = totalSalary;
    }

    public BigDecimal getYearSalary() {
        return yearSalary;
    }

    public void setYearSalary(BigDecimal yearSalary) {
        this.yearSalary = yearSalary;
    }
}
