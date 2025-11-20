package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import org.nishgrid.clienterp.dto.ClientDetailsDto;
import org.nishgrid.clienterp.service.ApiService;
import org.nishgrid.clienterp.util.ConfigManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CompanyDashboardController {

    @FXML private HBox contentBox;
    @FXML private Label statusLabel;
    @FXML private Label daysRemainingLabel;
    @FXML private Label endDateLabel;
    @FXML private Label startDateLabel;
    @FXML private Label companyNameLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label gstLabel;
    @FXML private Label addressLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private String licenseKey;


    @FXML
    public void initialize() {
        contentBox.setVisible(false);
        if (loadingIndicator != null) loadingIndicator.setVisible(true);

        ConfigManager config = ConfigManager.getInstance();
        this.licenseKey = config.getLicenseKey();

        if (licenseKey == null || licenseKey.isBlank()) {
            showError("License key not found in configuration.");
            return;
        }

        loadDashboardData();
    }

    private void loadDashboardData() {
        contentBox.setVisible(false);
        if (loadingIndicator != null) loadingIndicator.setVisible(true);

        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                // ✅ Fetch Client + License data using new endpoint
                ClientDetailsDto clientInfo = fetchClientByLicense(mapper, licenseKey);
                if (clientInfo == null) {
                    showError("No client data found for this license.");
                    return;
                }


                String startDate = clientInfo.getLicenseDetails() != null
                        ? clientInfo.getLicenseDetails().getStartDate()
                        : "N/A";
                String endDate = clientInfo.getLicenseDetails() != null
                        ? clientInfo.getLicenseDetails().getEndDate()
                        : "N/A";

                LicenseCalculations calc = calculateLicenseStatus(endDate);

                Platform.runLater(() -> {
                    updateUI(clientInfo, startDate, endDate, calc);
                    if (loadingIndicator != null) loadingIndicator.setVisible(false);
                    contentBox.setVisible(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showError("Error fetching data: " + e.getMessage()));
            }
        }).start();
    }


    private ClientDetailsDto fetchClientByLicense(ObjectMapper mapper, String licenseKey) {
        try {

            String endpoint = ApiService.getBaseUrl() + "/clients/by-license/" + licenseKey;

            HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("API error: " + responseCode);
                return null;
            }

            try (InputStream inputStream = conn.getInputStream()) {
                return mapper.readValue(inputStream, ClientDetailsDto.class);
            }

        } catch (Exception e) {
            System.err.println("Error fetching client by license: " + e.getMessage());
            return null;
        }
    }


    private void updateUI(ClientDetailsDto client, String startDate, String endDate, LicenseCalculations calc) {
        statusLabel.setText(calc.status());
        statusLabel.setStyle(calc.style());
        daysRemainingLabel.setText(calc.daysRemaining() + " days left");
        daysRemainingLabel.setStyle(calc.style());
        startDateLabel.setText(startDate);
        endDateLabel.setText(endDate);

        companyNameLabel.setText(client.getCompanyName());
        fullNameLabel.setText(client.getFullName());
        emailLabel.setText(client.getEmailAddress());
        gstLabel.setText(client.getGstNumber());
        addressLabel.setText(String.format("%s, %s, %s, %s - %s",
                client.getAddress(),
                client.getCity(),
                client.getState(),
                client.getCountry(),
                client.getPincode()));
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            if (loadingIndicator != null) loadingIndicator.setVisible(false);
            contentBox.setVisible(true);
            companyNameLabel.setText(message);
            statusLabel.setText("Failed");
            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
            daysRemainingLabel.setText("N/A");
        });
    }

    private LicenseCalculations calculateLicenseStatus(String endDateStr) {
        try {
            LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
            if (daysRemaining <= 0)
                return new LicenseCalculations(0, "Expired", "-fx-text-fill: red; -fx-font-weight: bold;");
            else if (daysRemaining <= 10)
                return new LicenseCalculations(daysRemaining, "Expiring Soon", "-fx-text-fill: orange; -fx-font-weight: bold;");
            else
                return new LicenseCalculations(daysRemaining, "Active", "-fx-text-fill: green;");
        } catch (Exception e) {
            return new LicenseCalculations(0, "Invalid Date", "-fx-text-fill: red;");
        }
    }

    private record LicenseCalculations(long daysRemaining, String status, String style) {}
}
