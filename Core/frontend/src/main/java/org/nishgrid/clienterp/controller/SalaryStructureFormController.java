package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.service.ApiService;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SalaryStructureFormController {

    @FXML private TextField employeeCodeField;
    @FXML private TextField basicSalaryField;
    @FXML private TextField hraField;
    @FXML private TextField otherAllowancesField;
    @FXML private TextField pfDeductionField;
    @FXML private TextField esiDeductionField;
    @FXML private TextField tdsDeductionField;

    @FXML private Label statusLabel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void handleSubmit() {
        statusLabel.setStyle("-fx-text-fill: black;");
        statusLabel.setText("");

        if (!validateForm()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("employeeCode", employeeCodeField.getText().trim());
        payload.put("basicSalary", new BigDecimal(basicSalaryField.getText().trim()));
        payload.put("hra", new BigDecimal(hraField.getText().trim()));
        payload.put("otherAllowances", new BigDecimal(otherAllowancesField.getText().trim()));
        payload.put("pfDeduction", new BigDecimal(pfDeductionField.getText().trim()));
        payload.put("esiDeduction", new BigDecimal(esiDeductionField.getText().trim()));
        payload.put("tdsDeduction", new BigDecimal(tdsDeductionField.getText().trim()));

        statusLabel.setText("Submitting...");

        new Thread(() -> sendRequest(payload)).start();
    }

    private void sendRequest(Map<String, Object> payload) {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/salary");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = objectMapper.writeValueAsString(payload);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();

            Platform.runLater(() -> {
                if (responseCode == 200 || responseCode == 201) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Salary structure saved successfully.");
                    clearForm();
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setText("Saved successfully.");
                } else if (responseCode == 404) {
                    showAlert(Alert.AlertType.WARNING, "Employee Not Found", "Employee code not found.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Employee not found.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: HTTP " + responseCode);
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Unexpected error.");
                }
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Exception", e.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error: " + e.getMessage());
            });
        }
    }

    private boolean validateForm() {
        if (employeeCodeField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Employee Code is required.");
            return false;
        }

        if (!isNumeric(basicSalaryField.getText(), "Basic Salary")) return false;
        if (!isNumeric(hraField.getText(), "HRA")) return false;
        if (!isNumeric(otherAllowancesField.getText(), "Other Allowances")) return false;
        if (!isNumeric(pfDeductionField.getText(), "PF Deduction")) return false;
        if (!isNumeric(esiDeductionField.getText(), "ESI Deduction")) return false;
        if (!isNumeric(tdsDeductionField.getText(), "TDS Deduction")) return false;

        return true;
    }

    private boolean isNumeric(String value, String fieldName) {
        try {
            new BigDecimal(value.trim());
            return true;
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", fieldName + " must be a valid number.");
            return false;
        }
    }

    private void clearForm() {
        employeeCodeField.clear();
        basicSalaryField.clear();
        hraField.clear();
        otherAllowancesField.clear();
        pfDeductionField.clear();
        esiDeductionField.clear();
        tdsDeductionField.clear();
        statusLabel.setText("");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
