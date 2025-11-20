package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;

public class SalaryStructureRequest {

    private String employeeCode;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal otherAllowances;
    private BigDecimal pfDeduction;
    private BigDecimal esiDeduction;
    private BigDecimal tdsDeduction;

    // Getters & Setters

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
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
}
