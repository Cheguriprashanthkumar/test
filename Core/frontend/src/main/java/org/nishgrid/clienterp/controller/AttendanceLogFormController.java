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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.stream.Collectors;

public class
AttendanceLogFormController {


    @FXML private TextField employeeCodeField;
    @FXML private DatePicker datePicker;
    @FXML private TextField checkInTimeField;
    @FXML private TextField checkOutTimeField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label messageLabel;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @FXML
    private void handleSubmit() {
        messageLabel.setText(""); // Clear previous messages

        // Validate Employee Code
        String employeeCode = employeeCodeField.getText().trim();
        if (employeeCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Employee Code is required.");
            return;
        }

        // Validate Date from DatePicker
        LocalDate date = datePicker.getValue();
        if (date == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Date is required.");
            return;
        }

        // Validate Times
        String checkInText = checkInTimeField.getText().trim();
        String checkOutText = checkOutTimeField.getText().trim();
        LocalTime checkInTime, checkOutTime;
        try {
            checkInTime = LocalTime.parse(checkInText);
            checkOutTime = LocalTime.parse(checkOutText);
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid time format. Use HH:mm:ss.");
            return;
        }

        // Validate Status
        String status = statusComboBox.getValue();
        if (status == null || status.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a status.");
            return;
        }

        // Build JSON payload to send to the backend
        String jsonPayload = String.format("""
                {
                  "employeeCode": "%s",
                  "date": "%s",
                  "checkInTime": "%s",
                  "checkOutTime": "%s",
                  "presentStatus": "%s"
                }
                """, employeeCode, date.format(DateTimeFormatter.ISO_LOCAL_DATE), checkInTime, checkOutTime, status);

        messageLabel.setText("Submitting...");
        sendPostRequest(jsonPayload);
    }


    private void sendPostRequest(String jsonPayload) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    URL url = new URL(ApiService.getBaseUrl()+"/attendance"); // POST endpoint
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(jsonPayload.getBytes());
                        os.flush();
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) { // OK
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Attendance logged successfully.");
                            resetForm();
                        });
                    } else {
                        // Handle backend errors
                        String errorMsg = parseError(conn);
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Submission Failed", errorMsg));
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "An exception occurred: " + e.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    // Helper methods for resetting the form and showing alerts
    private void resetForm() {
        employeeCodeField.clear();
        datePicker.setValue(null);
        checkInTimeField.clear();
        checkOutTimeField.clear();
        statusComboBox.setValue(null);
        messageLabel.setText("");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // In your AttendanceLogFormController.java

    private String parseError(HttpURLConnection conn) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
            String body = reader.lines().collect(Collectors.joining("\n"));

            // Use a more specific Map type instead of Map<?, ?>
            Map<String, Object> errorMap = objectMapper.readValue(body, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});

            // This line will now work correctly without errors
            return errorMap.getOrDefault("message", "An unknown error occurred.").toString();

        } catch (Exception e) {
            // This catch block handles cases where the error response isn't valid JSON
            return "Could not parse the error response from the server.";
        }
    }
}