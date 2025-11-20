package org.nishgrid.clienterp.dto;

import java.time.LocalDateTime;

public class GstExportDTO {
    private Long gstExportId;
    private String monthYear;
    private LocalDateTime generatedOn;
    private String filePath;
    private String status;
    private LocalDateTime submittedOn;

    // A no-argument constructor is needed for JSON deserialization
    public GstExportDTO() {
    }

    // Getters and Setters for all fields

    public Long getGstExportId() {
        return gstExportId;
    }

    public void setGstExportId(Long gstExportId) {
        this.gstExportId = gstExportId;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public LocalDateTime getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(LocalDateTime generatedOn) {
        this.generatedOn = generatedOn;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(LocalDateTime submittedOn) {
        this.submittedOn = submittedOn;
    }
}