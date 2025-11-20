package org.nishgrid.clienterp.controller;

import com.google.gson.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.LicenseResponse;
import org.nishgrid.clienterp.service.LicenseApiService;
import org.nishgrid.clienterp.util.ConfigManager;
import org.nishgrid.clienterp.util.SystemInfo;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LicenseKeyController {

    @FXML
    private TextField licenseKeyField;

    private final LicenseApiService apiService = new LicenseApiService();

    // ⭐ SAFE GSON (No LocalDate reflection)
    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(LicenseResponse.class, (JsonSerializer<LicenseResponse>) (src, typeOfSrc, context) -> {
                JsonObject json = new JsonObject();
                json.addProperty("uniqueId", src.getUniqueId());
                json.addProperty("licenseKey", src.getLicenseKey());
                json.addProperty("systemId", src.getSystemId());
                json.addProperty("startDate", src.getStartDate());
                json.addProperty("endDate", src.getEndDate());
                json.addProperty("setupCompleted", src.isSetupCompleted());
                json.addProperty("clientDetailsCompleted", src.isClientDetailsCompleted());
                json.addProperty("valid", src.isValid());
                json.addProperty("fullName", src.getFullName());
                json.addProperty("companyName", src.getCompanyName());
                json.addProperty("emailAddress", src.getEmailAddress());
                json.addProperty("redirectUrl", src.getRedirectUrl());
                json.addProperty("companyAddress", src.getCompanyAddress());
                return json;
            })
            .create();

    @FXML
    private void handleOk() {
        String licenseKey = licenseKeyField.getText().trim();
        if (licenseKey.isEmpty()) {
            showAlert("Please enter a license key.");
            return;
        }

        LicenseResponse response = apiService.validateLicense(licenseKey);

        if (response == null) {
            showAlert("⚠ Network error or no response.");
            return;
        }

        if (response.getFullName() == null || response.getLicenseKey() == null) {
            showAlert("❌ Validation Failed: " +
                    (response.getMessage() != null ? response.getMessage() : "Unknown error"));
            return;
        }

        // ⭐ After external validation → save locally
        saveLicenseToLocalDB(response);

        openNextScreen(response);
    }

    @FXML
    private void handleCancel() {
        Platform.exit();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    private void saveLicenseToLocalDB(LicenseResponse response) {
        try {
            URL url = new URL("https://client-nishgrid.co.in/ngst/api/license/save-valid");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = gson.toJson(response);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            conn.getInputStream().close();

        } catch (Exception e) {
            System.err.println("❌ Failed to store license locally: " + e.getMessage());
            System.err.println("Restoring data: " + gson.toJson(response));
        }
    }

    private void openNextScreen(LicenseResponse response) {
        try {
            ConfigManager config = ConfigManager.getInstance();

            String currentSystemId = SystemInfo.getSystemId();
            String storedSystemId = config.getSystemId();

            if (storedSystemId != null && !storedSystemId.equals(currentSystemId)) {
                showAlert("⚠ License belongs to another system.\nContact NishGrid.");
                return;
            }

            config.saveLicenseDetails(response, currentSystemId, true, false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client_details.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Client Details Form");
            stage.show();

            ClientDetailsController controller = loader.getController();
            controller.setLicenseResponse(response);

            Stage currentStage = (Stage) licenseKeyField.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Unable to open client form.\n" + e.getMessage());
        }
    }
}
