package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.SalesSummaryReport.ReportType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReportGenerationRequest {

    @NotNull(message = "Report type cannot be null")
    private ReportType reportType;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    private LocalDate endDate; // Null unless reportType is CUSTOM

    // Getters and Setters
    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}