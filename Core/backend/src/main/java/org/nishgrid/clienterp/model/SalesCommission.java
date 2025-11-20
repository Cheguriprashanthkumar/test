package org.nishgrid.clienterp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sales_commissions")
public class SalesCommission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commissionId;

    private String commissionRate; // E.g., "5%" or "1000"
    private Double amountTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private SalesInvoice salesInvoice;

    public Long getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(Long commissionId) {
        this.commissionId = commissionId;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public SalesInvoice getSalesInvoice() {
        return salesInvoice;
    }

    public void setSalesInvoice(SalesInvoice salesInvoice) {
        this.salesInvoice = salesInvoice;
    }
}