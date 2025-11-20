package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.nishgrid.clienterp.model.LicenseResponse;
import org.nishgrid.clienterp.util.ConfigManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Admin1DashboardController {

    @FXML private StackPane mainContent;
    @FXML private Label dateTimeLabel;
    @FXML private Label userNameLabel;
    @FXML private Label expiryDaysLabel;
    @FXML private Label clientCompanyLabel;
    @FXML private Label developedByLabel;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private PauseTransition inactivityTimer;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = createMapper();

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @FXML
    public void initialize() {
        handleHome();
        startClock();
        loadClientDetailsFromAPI();
        Platform.runLater(() -> {
            setupInactivityLogout();
            try {
                Stage stage = (Stage) mainContent.getScene().getWindow();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/favicon.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void startClock() {
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> dateTimeLabel.setText(LocalDateTime.now().format(dateTimeFormatter))),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void loadView(String fxml) {
        try {
            Node view = FXMLLoader.load(getClass().getResource("/fxml/" + fxml));
            mainContent.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClientDetailsFromAPI() {
        String licenseKey = ConfigManager.getInstance().getLicenseKey();
        if (licenseKey == null || licenseKey.isBlank()) {
            showLicenseError("License key missing. Please enter a valid key.");
            return;
        }

        Task<LicenseResponse> loadTask = new Task<>() {
            @Override
            protected LicenseResponse call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://client-nishgrid.co.in/ngst/api/license/by-key/" + licenseKey))
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (httpResponse.statusCode() != 200) {
                    throw new RuntimeException("Failed to fetch license details. Response: " + httpResponse.statusCode());
                }

                LicenseResponse response = objectMapper.readValue(httpResponse.body(), LicenseResponse.class);

                return response;
            }
        };

        loadTask.setOnSucceeded(event -> {
            LicenseResponse response = loadTask.getValue();
            updateDashboard(response);
        });

        loadTask.setOnFailed(event -> {
            Throwable exception = loadTask.getException();
            exception.printStackTrace();
            showLicenseError(exception.getMessage());
        });

        new Thread(loadTask).start();
    }

    private void updateDashboard(LicenseResponse response) {
        clientCompanyLabel.setText(response.getCompanyName() != null ? response.getCompanyName() : "Unknown Company");
        userNameLabel.setText(response.getFullName() != null ? response.getFullName() : "Unknown User");
        developedByLabel.setText("Developed by NishGrid Pvt Ltd");

        // --- Updated Logic ---
        // Always try to calculate expiry days, regardless of the 'isValid' flag.
        try {
            LocalDate expiryDate = LocalDate.parse(response.getEndDate());
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);

            if (daysLeft <= 0) {
                expiryDaysLabel.setText("Expired");
                expiryDaysLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                expiryDaysLabel.setText(daysLeft + " days left");

                // Reset style to default in case it was previously red
                expiryDaysLabel.setStyle(null);

                if (daysLeft <= 10) {
                    // Set to red only if 10 days or less
                    expiryDaysLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        } catch (Exception e) {
            // Catches errors if endDate is null or in a bad format
            expiryDaysLabel.setText("N/A");
            expiryDaysLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleCheckUpdate() {
        System.out.println("Checking for updates initiated from dashboard.");


        org.nishgrid.clienterp.util.UpdateManager.checkForUpdates(() -> {

            Alert restartAlert = new Alert(Alert.AlertType.INFORMATION);
            restartAlert.setTitle("Restart Required");
            restartAlert.setHeaderText("Update Complete!");
            restartAlert.setContentText("The application has been successfully updated. Please restart the application now.");
            restartAlert.showAndWait();


            javafx.application.Platform.exit();
        });
    }

    private void showLicenseError(String message) {
        Platform.runLater(() -> {
            clientCompanyLabel.setText("Unknown Company");
            userNameLabel.setText("Unknown User");
            expiryDaysLabel.setText("License Error");
            developedByLabel.setText(message);
        });
    }

    private void setupInactivityLogout() {
        inactivityTimer = new PauseTransition(Duration.minutes(10));
        inactivityTimer.setOnFinished(event -> handleLogout());
        EventHandler<javafx.event.Event> activityHandler = event -> inactivityTimer.playFromStart();
        if (mainContent.getScene() != null) {
            mainContent.getScene().addEventFilter(MouseEvent.ANY, activityHandler);
            mainContent.getScene().addEventFilter(KeyEvent.ANY, activityHandler);
            inactivityTimer.playFromStart();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/fxml/login_form.fxml"));
            mainContent.getScene().setRoot(loginView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleDashboard() { loadView("DashboardContent.fxml"); }
    @FXML private void handleNewSale() { loadView("NewSale.fxml"); }
    @FXML private void handleInvoices() { loadView("Invoices.fxml"); }
    @FXML private void handleCommisions() { loadView("sales_commissions.fxml"); }
    @FXML private void handleReturnSale() { loadView("ReturnSale.fxml"); }
    @FXML private void handleExchangeItem() { loadView("ExchangeItem.fxml"); }
    @FXML private void handleDiscountConfig() { loadView("discount_config.fxml"); }
    @FXML private void handleViewGoldRates() { loadView("gold_rates.fxml"); }
    @FXML private void handleSmsLogs() { loadView("sms_logs.fxml"); }
    @FXML private void handleEmailLogs() { loadView("email_logs.fxml"); }
    @FXML private void handleGiftVouchers() { loadView("gift_vouchers.fxml"); }
    @FXML private void handleCustomerKyc() { loadView("kyc_form.fxml"); }
    @FXML private void handleGstr1() { loadView("gstr1.fxml"); }
    @FXML private void handleGstr3b() { loadView("gstr3b.fxml"); }
    @FXML private void handleHsnSummary() { loadView("hsn_summary.fxml"); }
    @FXML private void handleExports() { loadView("gstexports.fxml"); }
    @FXML private void handleHome() { loadView("Home.fxml"); }
    @FXML private void handleVendorProduct() { loadView("VendorProductView.fxml"); }
    @FXML private void handlePurchaseOrders() { loadView("PurchaseOrderView.fxml"); }
    @FXML private void handleGrn() { loadView("GrnView.fxml"); }
    @FXML private void handleVendorInvoices() { loadView("PurchaseInvoiceView.fxml"); }
    @FXML private void handlePayments() { loadView("VendorPaymentView.fxml"); }
    @FXML private void handlePurchaseReturns() { loadView("PurchaseReturnView.fxml"); }
    @FXML private void handlePurchaseAuditLogs() { loadView("LogView.fxml"); }
    @FXML private void handleAttachDocuments() { loadView("PurchaseDocumentView.fxml"); }
    @FXML private void handleBarcodeTracking() { loadView("BarcodeView.fxml"); }
    @FXML private void handleShowBackup() { loadView("BackupView.fxml"); }
    @FXML private void handleBankDetails() { loadView("BankDetailsView.fxml"); }
    @FXML private void handleSalesReport() { loadView("SalesReportContainer.fxml"); }
    @FXML private void handleOldGoldExchange() { loadView("oldgoldexchangeview.fxml"); }
    @FXML private void handleOldGoldAudit() { loadView("OldGoldExchangeAuditView.fxml"); }
    @FXML private void handleaddclient() { loadView("CompanyDetailsView.fxml"); }
    @FXML private void handlecompanyinfo() { loadView("CompanyDashboard.fxml"); }
}