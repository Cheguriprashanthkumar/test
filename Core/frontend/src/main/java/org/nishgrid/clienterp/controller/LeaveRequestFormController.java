package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.service.ApiService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaveRequestFormController {

    @FXML private TextField employeeCodeField;
    @FXML private TextField leaveTypeField;
    @FXML private TextField holidayNameField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TextField totalDaysField;
    @FXML private TextField approvedByField;
    @FXML private ComboBox<String> leavePaymentTypeCombo;
    @FXML private Label statusLabel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    public void initialize() {
        leavePaymentTypeCombo.getItems().addAll("Paid", "Unpaid");
    }

    @FXML
    public void handleSubmit() {
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: black;");

        String employeeCode = employeeCodeField.getText().trim();
        if (employeeCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Employee code is required.");
            return;
        }

        String leaveType = leaveTypeField.getText().trim();
        if (leaveType.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Leave type is required.");
            return;
        }

        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "From Date and To Date are required.");
            return;
        }

        String totalDaysStr = totalDaysField.getText().trim();
        if (totalDaysStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Total days are required.");
            return;
        }

        int totalDays;
        try {
            totalDays = Integer.parseInt(totalDaysStr);
            if (totalDays <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Total days must be a positive number.");
            return;
        }

        String approvedBy = approvedByField.getText().trim();
        if (approvedBy.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Approved By is required.");
            return;
        }

        String leavePaymentType = leavePaymentTypeCombo.getValue();
        if (leavePaymentType == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Leave payment type must be selected.");
            return;
        }

        String json = String.format("""
            {
              "employeeCode": "%s",
              "leaveType": "%s",
              "holidayName": "%s",
              "fromDate": "%s",
              "toDate": "%s",
              "totalDays": %d,
              "approvedBy": "%s",
              "leavePaymentType": "%s"
            }
            """,
                employeeCode,
                leaveType,
                holidayNameField.getText().trim(),
                fromDatePicker.getValue(),
                toDatePicker.getValue(),
                totalDays,
                approvedBy,
                leavePaymentType
        );

        statusLabel.setText("Submitting...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                sendRequest(json);
                return null;
            }
        };

        new Thread(task).start();
    }

    private void sendRequest(String json) {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/leave-requests");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Leave request submitted successfully.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setText("Leave request submitted.");
                    resetForm();
                });
            } else {
                String errorMsg = parseError(conn);
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.WARNING, "Error", errorMsg != null ? errorMsg : "Invalid input or employee not found.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText(errorMsg != null ? errorMsg : "Invalid input or employee not found.");
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
            // ignore
        }
        return null;
    }

    private void resetForm() {
        employeeCodeField.clear();
        leaveTypeField.clear();
        holidayNameField.clear();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        totalDaysField.clear();
        approvedByField.clear();
        leavePaymentTypeCombo.setValue(null);
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
