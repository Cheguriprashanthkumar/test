package org.nishgrid.clienterp.dto;

import jakarta.validation.constraints.*;

public class SalesCommissionDTO {
    private Long commissionId;

    @NotNull(message = "Employee ID cannot be null")
    private Integer employeeId;

    private String employeeName;

    @NotNull(message = "Invoice ID cannot be null")
    private Long invoiceId;

    private String invoiceNo;

    @NotBlank(message = "Commission Rate is required")
    @Pattern(regexp = "^(\\d+(\\.\\d+)?%?|\\d+(\\.\\d+)?)$", message = "Invalid rate format. E.g., '5%' or '1000'")
    private String commissionRate;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amountTotal;

    public Long getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(Long commissionId) {
        this.commissionId = commissionId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(String commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(Double amountTotal) {
        this.amountTotal = amountTotal;
    }
}