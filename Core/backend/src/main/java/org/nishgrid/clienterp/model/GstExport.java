package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gst_exports")
public class GstExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gstExportId;

    private String monthYear; // e.g., "2025-07"

    private LocalDateTime generatedOn;

    private String filePath; // Location of the .csv/.json/.xls file

    @Enumerated(EnumType.STRING)
    private GstExportStatus status;

    private LocalDateTime submittedOn;

    public enum GstExportStatus {
        PENDING,
        GENERATED,
        SUBMITTED
    }

    // Getters and Setters
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

    public GstExportStatus getStatus() {
        return status;
    }

    public void setStatus(GstExportStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(LocalDateTime submittedOn) {
        this.submittedOn = submittedOn;
    }
}