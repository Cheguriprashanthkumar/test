package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.service.ApiService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeExitFormController {

    @FXML private TextField employeeCodeField; // ✅ Renamed from employeeIdField
    @FXML private DatePicker exitDatePicker;
    @FXML private TextArea exitReasonArea;
    @FXML private TextField finalSettlementField;
    @FXML private TextArea feedbackNotesArea;
    @FXML private ComboBox<String> clearanceStatusCombo;
    @FXML private Label statusLabel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    public void initialize() {
        clearanceStatusCombo.getItems().addAll("Cleared", "Pending", "Hold");
    }

    @FXML
    public void handleSubmit() {
        statusLabel.setStyle("-fx-text-fill: black;");
        statusLabel.setText("");

        String employeeCode = employeeCodeField.getText().trim();
        if (employeeCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Employee Code is required.");
            return;
        }

        if (exitDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Exit Date is required.");
            return;
        }
        String exitDate = exitDatePicker.getValue().format(DateTimeFormatter.ISO_DATE);

        String exitReason = exitReasonArea.getText().trim();
        if (exitReason.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Exit Reason is required.");
            return;
        }

        String settlementText = finalSettlementField.getText().trim();
        if (settlementText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Final Settlement amount is required.");
            return;
        }

        BigDecimal finalSettlement;
        try {
            finalSettlement = new BigDecimal(settlementText);
            if (finalSettlement.compareTo(BigDecimal.ZERO) < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Final Settlement cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Final Settlement must be a valid number.");
            return;
        }

        String feedbackNotes = feedbackNotesArea.getText().trim();

        String clearanceStatus = clearanceStatusCombo.getValue();
        if (clearanceStatus == null || clearanceStatus.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select Clearance Status.");
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("employeeCode", employeeCode);
        payload.put("exitDate", exitDate);
        payload.put("exitReason", exitReason);
        payload.put("finalSettlement", finalSettlement);
        payload.put("feedbackNotes", feedbackNotes);
        payload.put("clearanceStatus", clearanceStatus);

        statusLabel.setText("Submitting...");
        new Thread(() -> sendRequest(payload)).start();
    }

    private void sendRequest(Map<String, Object> payload) {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/exits");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = objectMapper.writeValueAsString(payload);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int code = conn.getResponseCode();

            if (code == 200) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Exit recorded successfully.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setText("Exit recorded.");
                    resetForm();
                });
            } else {
                String errorMsg = parseError(conn);
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.WARNING, "Error", errorMsg != null ? errorMsg : "Something went wrong. Please check Employee Code.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText(errorMsg != null ? errorMsg : "Something went wrong.");
                });
            }

            conn.disconnect();

        } catch (Exception e) {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Exception", e.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error: " + e.getMessage());
            });
        }
    }

    private String parseError(HttpURLConnection conn) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream()))) {
            String body = reader.lines().collect(Collectors.joining("\n"));

            if (!body.isBlank()) {
                try {
                    Map<?, ?> errorMap = objectMapper.readValue(body, Map.class);
                    Object msg = errorMap.get("message");
                    if (msg != null) {
                        return msg.toString();
                    }
                } catch (Exception ignored) {
                    return body;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void resetForm() {
        employeeCodeField.clear();
        exitDatePicker.setValue(null);
        exitReasonArea.clear();
        finalSettlementField.clear();
        feedbackNotesArea.clear();
        clearanceStatusCombo.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
