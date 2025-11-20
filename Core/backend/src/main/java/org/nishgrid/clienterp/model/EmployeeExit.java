package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee_exit")
public class EmployeeExit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer exitId;
    @NotNull(message = "Employee is required")
    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "employeeCode")
    private Employee employee;

    private LocalDate exitDate;

    @Column(columnDefinition = "TEXT")
    private String exitReason;

    private BigDecimal finalSettlement;

    @Column(columnDefinition = "TEXT")
    private String feedbackNotes;

    private String clearanceStatus; // Cleared / Pending / Hold

    // Getters and Setters
    public Integer getExitId() {
        return exitId;
    }

    public void setExitId(Integer exitId) {
        this.exitId = exitId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getExitDate() {
        return exitDate;
    }

    public void setExitDate(LocalDate exitDate) {
        this.exitDate = exitDate;
    }

    public String getExitReason() {
        return exitReason;
    }

    public void setExitReason(String exitReason) {
        this.exitReason = exitReason;
    }

    public BigDecimal getFinalSettlement() {
        return finalSettlement;
    }

    public void setFinalSettlement(BigDecimal finalSettlement) {
        this.finalSettlement = finalSettlement;
    }

    public String getFeedbackNotes() {
        return feedbackNotes;
    }

    public void setFeedbackNotes(String feedbackNotes) {
        this.feedbackNotes = feedbackNotes;
    }

    public String getClearanceStatus() {
        return clearanceStatus;
    }

    public void setClearanceStatus(String clearanceStatus) {
        this.clearanceStatus = clearanceStatus;
    }
}
