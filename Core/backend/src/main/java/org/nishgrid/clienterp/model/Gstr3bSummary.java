package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gstr3b_summary")
public class Gstr3bSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String monthYear; // Month of filing, e.g., "2025-07"

    private Double outwardTaxableSupplies;

    private Double inwardSupplies;

    private Double igst;

    private Double cgst;

    private Double sgst;

    @Enumerated(EnumType.STRING)
    private GstFiledStatus filedStatus;

    private LocalDateTime filedDate;

    public enum GstFiledStatus {
        PENDING,
        SUBMITTED,
        PAID
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public Double getOutwardTaxableSupplies() {
        return outwardTaxableSupplies;
    }

    public void setOutwardTaxableSupplies(Double outwardTaxableSupplies) {
        this.outwardTaxableSupplies = outwardTaxableSupplies;
    }

    public Double getInwardSupplies() {
        return inwardSupplies;
    }

    public void setInwardSupplies(Double inwardSupplies) {
        this.inwardSupplies = inwardSupplies;
    }

    public Double getIgst() {
        return igst;
    }

    public void setIgst(Double igst) {
        this.igst = igst;
    }

    public Double getCgst() {
        return cgst;
    }

    public void setCgst(Double cgst) {
        this.cgst = cgst;
    }

    public Double getSgst() {
        return sgst;
    }

    public void setSgst(Double sgst) {
        this.sgst = sgst;
    }

    public GstFiledStatus getFiledStatus() {
        return filedStatus;
    }

    public void setFiledStatus(GstFiledStatus filedStatus) {
        this.filedStatus = filedStatus;
    }

    public LocalDateTime getFiledDate() {
        return filedDate;
    }

    public void setFiledDate(LocalDateTime filedDate) {
        this.filedDate = filedDate;
    }
}