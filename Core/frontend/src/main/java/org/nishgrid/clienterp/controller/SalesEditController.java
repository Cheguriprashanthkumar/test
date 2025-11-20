package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.nishgrid.clienterp.dto.SaleUpdateDTO;
import org.nishgrid.clienterp.dto.SalesInvoiceDto;
import org.nishgrid.clienterp.service.ApiService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SalesEditController {

    @FXML private Label invoiceNoLabel;
    @FXML private DatePicker invoiceDatePicker;
    @FXML private TextField paymentModeField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea remarksArea;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private Long currentInvoiceId;
    private boolean saved = false;

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList( "CANCELLED"));
    }

    public void loadInvoiceData(Long invoiceId) {
        this.currentInvoiceId = invoiceId;
        String url = ApiService.getBaseUrl() + "/sales/" + invoiceId;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            SalesInvoiceDto invoice = objectMapper.readValue(response.body(), SalesInvoiceDto.class);
                            Platform.runLater(() -> populateForm(invoice));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void populateForm(SalesInvoiceDto invoice) {
        invoiceNoLabel.setText(invoice.getInvoiceNo());
        invoiceDatePicker.setValue(invoice.getInvoiceDate());
        paymentModeField.setText(invoice.getPaymentMode());
        statusComboBox.setValue(invoice.getStatus());
        remarksArea.setText(invoice.getRemarks());
    }

    @FXML
    private void handleSave() {
        SaleUpdateDTO updateDto = new SaleUpdateDTO();
        updateDto.setInvoiceDate(invoiceDatePicker.getValue());
        updateDto.setPaymentMode(paymentModeField.getText());
        updateDto.setStatus(statusComboBox.getValue());
        updateDto.setRemarks(remarksArea.getText());

        try {
            String requestBody = objectMapper.writeValueAsString(updateDto);
            String url = ApiService.getBaseUrl() + "/sales/" + currentInvoiceId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            this.saved = true;
                            Platform.runLater(this::handleCancel);
                        } else {
                            Platform.runLater(() -> showAlert("Update Failed", "Server says: " + response.body()));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) invoiceNoLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
