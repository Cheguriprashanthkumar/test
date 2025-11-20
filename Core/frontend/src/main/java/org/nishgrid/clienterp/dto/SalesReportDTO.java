package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesReportDTO {
    private Long reportId;
    private LocalDate reportDate;
    private String reportType;
    private BigDecimal totalSales;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private int totalItemsSold;
    private int totalCustomers;
    private LocalDateTime generatedOn;

    // Getters and Setters
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    public BigDecimal getTotalTax() { return totalTax; }
    public void setTotalTax(BigDecimal totalTax) { this.totalTax = totalTax; }
    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigDecimal totalDiscount) { this.totalDiscount = totalDiscount; }
    public int getTotalItemsSold() { return totalItemsSold; }
    public void setTotalItemsSold(int totalItemsSold) { this.totalItemsSold = totalItemsSold; }
    public int getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }
    public LocalDateTime getGeneratedOn() { return generatedOn; }
    public void setGeneratedOn(LocalDateTime generatedOn) { this.generatedOn = generatedOn; }
}