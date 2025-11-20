package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;
import org.nishgrid.clienterp.service.ApiService;
import org.nishgrid.clienterp.util.ConfigManager;
import org.nishgrid.clienterp.util.SystemInfo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LoginFormController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void initialize() {
        updateDateTime();
    }

    private void updateDateTime() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText("Date: " + now.format(dateFormat));
        timeLabel.setText("Time: " + now.format(timeFormat));
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            setMessage("Please enter both Email and Password.", "red");
            return;
        }

        Map<String, String> loginPayload = Map.of("email", email, "password", password);

        try {
            String loginEndpoint = ApiService.getBaseUrl() + "/login";
            Map<String, Object> localLoginResponse = postRequest(loginEndpoint, loginPayload);
            String localStatus = (String) localLoginResponse.get("status");
            String localMessage = (String) localLoginResponse.get("message");

            if ("SUCCESS".equalsIgnoreCase(localStatus)) {

                setMessage("Login successful. Verifying license status...", "blue");

                ConfigManager config = ConfigManager.getInstance();
                String licenseKey = config.getLicenseKey();
                String systemId = config.getSystemId();

                if (licenseKey == null || systemId == null) {
                    setMessage("CRITICAL ERROR: No license or system ID found in config.", "red");
                    return;
                }

                String validateEndpoint = ApiService.getLicenseUrl() + "/licenses/validate";
                Map<String, String> licensePayload = Map.of(
                        "licenseKey", licenseKey,
                        "systemId", systemId
                );

                Map<String, Object> licenseResponse = postRequest(validateEndpoint, licensePayload);

                if (licenseResponse.containsKey("uniqueId")) {
                    setMessage(localMessage, "green");
                    openDashboard("/fxml/Admin1Dashboard.fxml", "Dashboard-Estimated Software");
                    Stage currentStage = (Stage) loginButton.getScene().getWindow();
                    currentStage.close();

                } else if (licenseResponse.containsKey("error")) {

                    String serverError = (String) licenseResponse.get("error");
                    String errorStatus = (String) licenseResponse.get("status");

                    if ("BLOCKED".equalsIgnoreCase(errorStatus)) {
                        setMessage("Login failed: " + serverError, "red");
                    } else {
                        System.err.println("Ignoring license validation error: " + serverError);
                        setMessage(localMessage, "green");
                        openDashboard("/fxml/Admin1Dashboard.fxml", "Dashboard-Estimated Software");
                        Stage currentStage = (Stage) loginButton.getScene().getWindow();
                        currentStage.close();
                    }

                } else {
                    String errorMsg = (String) licenseResponse.getOrDefault("message", "Unknown license response.");
                    setMessage("Login failed: " + errorMsg, "red");
                }

            } else {
                setMessage(localMessage != null ? localMessage : "Login failed.", "red");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            setMessage("Error: Failed to connect to server. " + ex.getMessage(), "red");
        }
    }

    private Map<String, Object> postRequest(String urlStr, Map<String, String> payload) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            objectMapper.writeValue(os, payload);
        }

        int responseCode = conn.getResponseCode();
        InputStream is;

        if (responseCode < 400) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();

            if (is == null) {
                return Map.of(
                        "status", "ERROR",
                        "message", "Request failed (Code: " + responseCode + ")"
                );
            }
        }

        return objectMapper.readValue(is, Map.class);
    }

    private void openDashboard(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle(title);

            InputStream iconStream = getClass().getResourceAsStream("/images/logo.png");
            if (iconStream != null) {
                stage.getIcons().add(new javafx.scene.image.Image(iconStream));
            } else {
                System.err.println("Dashboard icon not found! Please place /images/logo.png in resources.");
            }

            stage.setScene(scene);
            stage.initStyle(StageStyle.DECORATED);
            stage.setResizable(true);
            stage.setWidth(1200);
            stage.setHeight(700);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            setMessage("Error opening dashboard: " + ex.getMessage(), "red");
        }
    }

    @FXML
    private void handleCloseApp() {
        Platform.exit();
    }

    private void setMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    private void handleForgotPassword() { }

    @FXML
    private void handleSignInClick() { }

    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximizeRestore(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleCloseApp(ActionEvent event) {
        Platform.exit();
    }
}