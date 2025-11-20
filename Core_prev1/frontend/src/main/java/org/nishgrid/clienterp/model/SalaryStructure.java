package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "salary_structures")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each Salary Structure is linked to one Employee
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode", unique = true)
    private Employee employee;

    private BigDecimal basicSalary;
    private BigDecimal hra; // House Rent Allowance
    private BigDecimal otherAllowances;

    // Deductions
    private BigDecimal pfDeduction; // Provident Fund
    private BigDecimal esiDeduction; // Employee State Insurance
    private BigDecimal tdsDeduction; // Tax Deducted at Source

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }
    public BigDecimal getHra() { return hra; }
    public void setHra(BigDecimal hra) { this.hra = hra; }
    public BigDecimal getOtherAllowances() { return otherAllowances; }
    public void setOtherAllowances(BigDecimal otherAllowances) { this.otherAllowances = otherAllowances; }
    public BigDecimal getPfDeduction() { return pfDeduction; }
    public void setPfDeduction(BigDecimal pfDeduction) { this.pfDeduction = pfDeduction; }
    public BigDecimal getEsiDeduction() { return esiDeduction; }
    public void setEsiDડeduction(BigDecimal esiDeduction) { this.esiDeduction = esiDeduction; }
    public BigDecimal getTdsDeduction() { return tdsDeduction; }
    public void setTdsDeduction(BigDecimal tdsDeduction) { this.tdsDeduction = tdsDeduction; }
}