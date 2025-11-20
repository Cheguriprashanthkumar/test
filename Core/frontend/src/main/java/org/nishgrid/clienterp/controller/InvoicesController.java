package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.nishgrid.clienterp.dto.InvoiceRowDTO;
import org.nishgrid.clienterp.dto.PaymentRequestDTO;
import org.nishgrid.clienterp.model.InvoiceRow;
import org.nishgrid.clienterp.service.ApiService;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvoicesController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterBox;
    @FXML private TableView<InvoiceRow> invoiceTable;
    @FXML private TableColumn<InvoiceRow, String> invoiceNoCol;
    @FXML private TableColumn<InvoiceRow, String> customerCol;
    @FXML private TableColumn<InvoiceRow, LocalDate> dateCol;
    @FXML private TableColumn<InvoiceRow, String> paymentModeCol;
    @FXML private TableColumn<InvoiceRow, Double> amountCol;
    @FXML private TableColumn<InvoiceRow, Double> netAmountCol;
    @FXML private TableColumn<InvoiceRow, Double> paidAmountCol;
    @FXML private TableColumn<InvoiceRow, Double> dueAmountCol;
    @FXML private TableColumn<InvoiceRow, String> statusCol;
    @FXML private TableColumn<InvoiceRow, Void> actionCol;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_BASE_URL = ApiService.getBaseUrl() + "/invoices";

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        loadInvoices();
    }

    private void setupTableColumns() {
        invoiceNoCol.setCellValueFactory(data -> data.getValue().invoiceNoProperty());
        customerCol.setCellValueFactory(data -> data.getValue().customerNameProperty());
        dateCol.setCellValueFactory(data -> data.getValue().invoiceDateProperty());
        paymentModeCol.setCellValueFactory(data -> data.getValue().paymentModeProperty());
        amountCol.setCellValueFactory(data -> data.getValue().totalAmountProperty().asObject());
        netAmountCol.setCellValueFactory(data -> data.getValue().netAmountProperty().asObject());
        paidAmountCol.setCellValueFactory(data -> data.getValue().paidAmountProperty().asObject());
        dueAmountCol.setCellValueFactory(data -> data.getValue().dueAmountProperty().asObject());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        actionCol.setCellFactory(createActionButtons());
    }

    private void setupFilters() {
        statusFilterBox.setItems(FXCollections.observableArrayList(
                "All", "Paid", "Pending", "Partially Paid", "Cancelled", "Partially Exchanged", "Partially Returned"
        ));
        statusFilterBox.getSelectionModel().select("All");
        statusFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> handleSearch());
    }

    private void loadInvoices() {
        String keyword = searchField.getText();
        String status = statusFilterBox.getValue();
        List<String> queryParams = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryParams.add("search=" + URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8));
        }
        if (status != null && !"All".equalsIgnoreCase(status)) {
            queryParams.add("status=" + URLEncoder.encode(status, StandardCharsets.UTF_8));
        }
        String url = API_BASE_URL;
        if (!queryParams.isEmpty()) {
            url += "?" + String.join("&", queryParams);
        }
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<InvoiceRowDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
                        List<InvoiceRow> fxList = dtoList.stream()
                                .map(this::convertDtoToInvoiceRow)
                                .collect(Collectors.toList());
                        Platform.runLater(() -> invoiceTable.setItems(FXCollections.observableArrayList(fxList)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load invoice data from the server.");
                            invoiceTable.setItems(FXCollections.observableArrayList(Collections.emptyList()));
                        });
                    }
                });
    }

    private InvoiceRow convertDtoToInvoiceRow(InvoiceRowDTO dto) {
        return new InvoiceRow(
                dto.getId(),
                dto.getInvoiceNo(),
                dto.getCustomerName(),
                dto.getInvoiceDate(),
                dto.getSalesType(),
                dto.getPaymentMode(),
                Optional.ofNullable(dto.getTotalAmount()).map(BigDecimal::doubleValue).orElse(0.0),
                Optional.ofNullable(dto.getNetAmount()).map(BigDecimal::doubleValue).orElse(0.0),
                Optional.ofNullable(dto.getPaidAmount()).map(BigDecimal::doubleValue).orElse(0.0),
                Optional.ofNullable(dto.getDueAmount()).map(BigDecimal::doubleValue).orElse(0.0),
                dto.getStatus(),
                Optional.ofNullable(dto.getOldGoldValue()).map(BigDecimal::doubleValue).orElse(0.0)
        );
    }

    @FXML
    private void handleSearch() {
        loadInvoices();
    }

    private void showAddPaymentDialog(InvoiceRow row) {
        Dialog<PaymentRequestDTO> dialog = new Dialog<>();
        dialog.setTitle("Add Payment for Invoice: " + row.getInvoiceNo());
        BigDecimal dueAmount = BigDecimal.valueOf(row.getDueAmount()).setScale(2, RoundingMode.HALF_UP);
        dialog.setHeaderText("Enter payment details. Amount due: " + dueAmount);
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount to pay");
        ComboBox<String> modeBox = new ComboBox<>(FXCollections.observableArrayList("Cash", "UPI", "Card", "Bank", "Other"));
        modeBox.getSelectionModel().selectFirst();
        TextField otherModeField = new TextField();
        otherModeField.setPromptText("Enter payment mode");
        otherModeField.setVisible(false);
        modeBox.valueProperty().addListener((obs, oldVal, newVal) -> otherModeField.setVisible("Other".equalsIgnoreCase(newVal)));
        TextField refField = new TextField();
        TextField receivedByField = new TextField();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Amount Due:"), new Label(String.valueOf(dueAmount)));
        grid.addRow(1, new Label("Enter Amount:"), amountField);
        grid.addRow(2, new Label("Mode:"), modeBox, otherModeField);
        grid.addRow(3, new Label("Reference No:"), refField);
        grid.addRow(4, new Label("Received By:"), receivedByField);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    BigDecimal enteredAmount = new BigDecimal(amountField.getText());
                    if (enteredAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Payment amount must be positive.");
                        return null;
                    }
                    if (enteredAmount.compareTo(dueAmount) > 0) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Payment amount exceeds the due amount.");
                        return null;
                    }
                    PaymentRequestDTO dto = new PaymentRequestDTO();
                    dto.setInvoiceId(row.getId());
                    dto.setAmount(enteredAmount);
                    dto.setMode("Other".equalsIgnoreCase(modeBox.getValue()) ? otherModeField.getText().trim() : modeBox.getValue());
                    dto.setReferenceNo(refField.getText());
                    dto.setReceivedBy(receivedByField.getText());
                    dto.setPaymentDate(LocalDate.now());
                    return dto;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid amount entered. Please enter a number.");
                    return null;
                }
            }
            return null;
        });
        Optional<PaymentRequestDTO> result = dialog.showAndWait();
        result.ifPresent(this::sendPaymentRequest);
    }

    private void sendPaymentRequest(PaymentRequestDTO dto) {
        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/" + dto.getInvoiceId() + "/payments"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            Platform.runLater(() -> {
                                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment recorded successfully!");
                                handleSearch();
                            });
                        } else {
                            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Payment Failed", "Server responded with error: " + response.body()));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Client Error", "An error occurred while sending the payment request."));
        }
    }

    private void printInvoice(InvoiceRow row) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/" + row.getId() + "/print"))
                .GET()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            Path tempFile = Files.createTempFile("invoice-" + row.getInvoiceNo() + "-", ".pdf");
                            Files.write(tempFile, response.body());
                            openOrSavePdfInBackground(tempFile.toFile(), row.getInvoiceNo());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "File Error", "Error creating temporary file."));
                        }
                    } else {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Print Error", "Failed to generate invoice PDF."));
                    }
                });
    }

    private void openOrSavePdfInBackground(File tempFile, String invoiceNo) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile);
            } else {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Save PDF", "Could not open PDF automatically. Please save the file.");
                    showSaveDialog(tempFile, invoiceNo);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.INFORMATION, "Save PDF", "Could not open the PDF automatically. Please save the file.");
                showSaveDialog(tempFile, invoiceNo);
            });
        }
    }

    private void showSaveDialog(File tempFile, String invoiceNo) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Invoice PDF");
        fileChooser.setInitialFileName("Invoice-" + invoiceNo + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File destFile = fileChooser.showSaveDialog(invoiceTable.getScene().getWindow());
        if (destFile != null) {
            new Thread(() -> {
                try {
                    Files.copy(tempFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice saved successfully to " + destFile.getAbsolutePath()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error Saving File", ex.getMessage()));
                }
            }).start();
        }
    }

    private Callback<TableColumn<InvoiceRow, Void>, TableCell<InvoiceRow, Void>> createActionButtons() {
        return param -> new TableCell<>() {
            private final Button printBtn = new Button("Print");
            private final Button payBtn = new Button("Pay");
            private final HBox hBox = new HBox(5, printBtn, payBtn);
            {
                printBtn.setOnAction(e -> Optional.ofNullable(getTableRow().getItem()).ifPresent(InvoicesController.this::printInvoice));
                payBtn.setOnAction(e -> Optional.ofNullable(getTableRow().getItem()).ifPresent(InvoicesController.this::showAddPaymentDialog));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    InvoiceRow row = getTableRow().getItem();
                    boolean isPayable = !"PAID".equalsIgnoreCase(row.getStatus()) && !"CANCELLED".equalsIgnoreCase(row.getStatus());
                    payBtn.setDisable(!isPayable);
                    setGraphic(hBox);
                }
            }
        };
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}