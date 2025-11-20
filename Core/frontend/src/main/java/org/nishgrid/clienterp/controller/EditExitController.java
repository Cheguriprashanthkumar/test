package org.nishgrid.clienterp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.EmployeeExit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.nishgrid.clienterp.service.ApiService;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

public class EditExitController {

    @FXML private TextField employeeCodeField;
    @FXML private DatePicker exitDatePicker;
    @FXML private TextField reasonField;
    @FXML private TextField settlementField;
    @FXML private TextField feedbackField;
    @FXML private ComboBox<String> statusComboBox;

    private boolean editMode = false;
    private EmployeeExit currentExit;

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("Pending", "Cleared", "Not Cleared");
    }

    public void setEditMode(boolean isEdit, EmployeeExit exit) {
        this.editMode = isEdit;
        this.currentExit = exit;

        if (editMode && exit != null) {
            employeeCodeField.setText(exit.getEmployeeCode());
            employeeCodeField.setDisable(true); // Make read-only
            exitDatePicker.setValue(exit.getExitDate());
            reasonField.setText(exit.getExitReason());
            settlementField.setText(String.valueOf(exit.getFinalSettlement()));
            feedbackField.setText(exit.getFeedbackNotes());
            statusComboBox.setValue(exit.getClearanceStatus());
        }
    }

    @FXML
    private void onSave() {
        if (currentExit == null) {
            showAlert("No data found to save.");
            return;
        }

        // Update object from UI
        currentExit.setEmployeeCode(employeeCodeField.getText());
        currentExit.setExitDate(exitDatePicker.getValue() != null ? exitDatePicker.getValue() : LocalDate.now());
        currentExit.setExitReason(reasonField.getText());

        try {
            double settlement = Double.parseDouble(settlementField.getText());
            currentExit.setFinalSettlement(settlement);
        } catch (NumberFormatException e) {
            showAlert("Invalid settlement amount. Please enter a valid number.");
            return;
        }

        currentExit.setFeedbackNotes(feedbackField.getText());
        currentExit.setClearanceStatus(statusComboBox.getValue());

        // PUT Request
        try {
            int id = currentExit.getExitId();
            if (id <= 0) {
                showAlert("Invalid Exit ID.");
                return;
            }

            URL url = new URL(ApiService.getBaseUrl()+"/exits/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // ✅ Properly configure ObjectMapper to handle LocalDate
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            String json = mapper.writeValueAsString(currentExit);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                showAlert("Failed to update. Server responded with code: " + responseCode);
                return;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error while saving: " + ex.getMessage());
            return;
        }

        // Close window on success
        closeStage();
    }

    @FXML
    private void onCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) employeeCodeField.getScene().getWindow();
        if (stage != null) stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
