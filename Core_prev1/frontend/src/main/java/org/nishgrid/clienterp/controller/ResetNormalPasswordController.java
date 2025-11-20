package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.nishgrid.clienterp.service.ApiService;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ResetNormalPasswordController {

    @FXML private TextField adminUserIdField;
    @FXML private TextField normalUserIdField;
    @FXML private PasswordField newPasswordField;
    @FXML private Button submitButton;
    @FXML private Label messageLabel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void initialize() {
        submitButton.setOnAction(e -> handleSubmit());
    }

    private void handleSubmit() {
        String adminId = adminUserIdField.getText().trim();
        String normalId = normalUserIdField.getText().trim();
        String newPass = newPasswordField.getText();

        if (adminId.isEmpty() || normalId.isEmpty() || newPass.isEmpty()) {
            setMessage("All fields are required.", "red");
            return;
        }

        Map<String, String> payload = new HashMap<>();
        payload.put("adminUserId", adminId);
        payload.put("normalUserId", normalId);
        payload.put("newPassword", newPass);

        try {
            URL url = new URL(ApiService.getBaseUrl()+"/reset-password/normal");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                objectMapper.writeValue(os, payload);
            }

            if (conn.getResponseCode() == 200) {
                setMessage("Password reset successful!", "green");
            } else {
                setMessage("Failed: HTTP " + conn.getResponseCode(), "red");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            setMessage("Error: " + ex.getMessage(), "red");
        }
    }

    private void setMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
