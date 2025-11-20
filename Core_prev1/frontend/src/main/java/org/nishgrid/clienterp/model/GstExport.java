package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class GstExport {
    private final IntegerProperty gstExportId;
    private final StringProperty monthYear;
    private final StringProperty generatedOn;
    private final StringProperty filePath;
    private final StringProperty status;
    private final StringProperty submittedOn;

    public GstExport(int gstExportId, String monthYear, String generatedOn, String filePath, String status, String submittedOn) {
        this.gstExportId = new SimpleIntegerProperty(gstExportId);
        this.monthYear = new SimpleStringProperty(monthYear);
        this.generatedOn = new SimpleStringProperty(generatedOn);
        this.filePath = new SimpleStringProperty(filePath);
        this.status = new SimpleStringProperty(status);
        this.submittedOn = new SimpleStringProperty(submittedOn);
    }

    // --- Standard Getters Added ---
    public int getGstExportId() { return gstExportId.get(); }
    public String getMonthYear() { return monthYear.get(); }
    public String getGeneratedOn() { return generatedOn.get(); }
    public String getFilePath() { return filePath.get(); }
    public String getStatus() { return status.get(); }
    public String getSubmittedOn() { return submittedOn.get(); }

    // --- Property Getters ---
    public IntegerProperty gstExportIdProperty() { return gstExportId; }
    public StringProperty monthYearProperty() { return monthYear; }
    public StringProperty generatedOnProperty() { return generatedOn; }
    public StringProperty filePathProperty() { return filePath; }
    public StringProperty statusProperty() { return status; }
    public StringProperty submittedOnProperty() { return submittedOn; }
}