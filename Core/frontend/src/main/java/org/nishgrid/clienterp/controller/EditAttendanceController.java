package org.nishgrid.clienterp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.AttendanceLogFx;
import org.json.JSONObject;
import org.nishgrid.clienterp.service.ApiService;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;

public class EditAttendanceController {

    @FXML private TextField txtEmployeeCode;
    @FXML private DatePicker dpAttendanceDate;
    @FXML private TextField txtCheckInTime;
    @FXML private TextField txtCheckOutTime;
    @FXML private TextField txtWorkingHours;
    @FXML private ComboBox<String> cbPresentStatus;
    @FXML private TextField txtRemarks;
    @FXML private Button btnSave;

    private AttendanceLogFx attendanceData;

    // ✅ Called by parent controller
    public void setAttendanceLog(AttendanceLogFx data) {
        this.attendanceData = data;

        // Populate form fields
        txtEmployeeCode.setText(data.getEmployeeCode());
        dpAttendanceDate.setValue(data.getAttendanceDate());
        txtCheckInTime.setText(data.getCheckInTime() != null ? data.getCheckInTime().toString() : "");
        txtCheckOutTime.setText(data.getCheckOutTime() != null ? data.getCheckOutTime().toString() : "");
        txtWorkingHours.setText(data.getWorkingHours() != null ? data.getWorkingHours().toPlainString() : "");
        cbPresentStatus.setValue(data.getPresentStatus());
        txtRemarks.setText("Updated attendance log");
    }

    @FXML
    private void initialize() {
        cbPresentStatus.getItems().addAll("Present", "Absent", "Leave", "Half-Day");

        btnSave.setOnAction(e -> {
            try {
                JSONObject json = prepareJson();
                sendUpdateRequest(json);

                // Close current window
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Update failed: " + ex.getMessage());
            }
        });
    }

    private JSONObject prepareJson() {
        JSONObject json = new JSONObject();
        json.put("employeeCode", txtEmployeeCode.getText());
        json.put("date", dpAttendanceDate.getValue().toString());
        json.put("checkInTime", txtCheckInTime.getText());
        json.put("checkOutTime", txtCheckOutTime.getText());
        json.put("remarks", txtRemarks.getText());
        return json;
    }

    private void sendUpdateRequest(JSONObject data) throws Exception {
        URL url = new URL(ApiService.getBaseUrl()+"/attendance"); // ✅ Adjust if needed
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.toString().getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200 && responseCode != 204) {
            throw new RuntimeException("HTTP error code: " + responseCode);
        }

        conn.disconnect();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Update Error");
        alert.setHeaderText("Failed to update attendance");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
