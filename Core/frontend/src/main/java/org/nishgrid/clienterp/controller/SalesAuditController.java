package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.dto.SalesListResponse;
import org.nishgrid.clienterp.dto.SalesRecordDto;
import org.nishgrid.clienterp.model.SalesRecordFX;
import org.nishgrid.clienterp.service.ApiService;
import org.nishgrid.clienterp.util.DateUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SalesAuditController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<SalesRecordFX> salesTable;
    @FXML private TableColumn<SalesRecordFX, Long> idCol;
    @FXML private TableColumn<SalesRecordFX, String> invoiceNoCol;
    @FXML private TableColumn<SalesRecordFX, LocalDate> invoiceDateCol;
    @FXML private TableColumn<SalesRecordFX, String> customerNameCol;
    @FXML private TableColumn<SalesRecordFX, BigDecimal> finalAmountCol;
    @FXML private TableColumn<SalesRecordFX, Void> actionsCol;
    @FXML private Label totalSalesAmountLabel, totalReturnedAmountLabel, finalAmountLabel;
    @FXML private Label totalOldGoldLabel, totalDueAmountLabel;
    @FXML private Label totalSalesAfterPaymentsLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_BASE_URL = ApiService.getBaseUrl();
    private final BigDecimal ZERO = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        DateUtils.restrictFutureDates(startDatePicker);
        DateUtils.restrictFutureDates(endDatePicker);

        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        setupTableColumns();
        addActionsToTable();
        loadSalesData();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        invoiceNoCol.setCellValueFactory(cellData -> cellData.getValue().invoiceNoProperty());
        invoiceDateCol.setCellValueFactory(cellData -> cellData.getValue().invoiceDateProperty());
        customerNameCol.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        finalAmountCol.setCellValueFactory(cellData -> cellData.getValue().finalAmountProperty());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        finalAmountCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : currencyFormat.format(value));
            }
        });
    }

    @FXML
    private void handleFilter() {
        loadSalesData();
    }

    private void loadSalesData() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date Range", "Please select both a start and end date.");
            return;
        }

        String salesAuditUrl = String.format("%s/sales?startDate=%s&endDate=%s&status=Paid%%2CPartially%%20Returned", API_BASE_URL, startDate, endDate);
        HttpRequest salesAuditRequest = HttpRequest.newBuilder().uri(URI.create(salesAuditUrl)).GET().build();

        httpClient.sendAsync(salesAuditRequest, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            SalesListResponse salesList = objectMapper.readValue(response.body(), SalesListResponse.class);
                            List<SalesRecordDto> salesRecords = salesList.getSalesRecords();

                            List<SalesRecordFX> fxList = salesRecords.stream()
                                    .map(this::dtoToFx)
                                    .collect(Collectors.toList());

                            BigDecimal totalNetAmount = salesRecords.stream()
                                    .map(dto -> Optional.ofNullable(dto.getNetAmount()).orElse(ZERO))
                                    .reduce(ZERO, BigDecimal::add);

                            BigDecimal totalOldGoldAmount = salesRecords.stream()
                                    .map(dto -> Optional.ofNullable(dto.getOldGoldValue()).orElse(ZERO))
                                    .reduce(ZERO, BigDecimal::add);

                            // CORRECTED: Total Returned Amount = (Net Amount + Old Gold Value) - Final Amount
                            BigDecimal totalReturnedAmount = salesRecords.stream()
                                    .map(dto -> Optional.ofNullable(dto.getNetAmount()).orElse(ZERO)
                                            .add(Optional.ofNullable(dto.getOldGoldValue()).orElse(ZERO))
                                            .subtract(Optional.ofNullable(dto.getFinalAmount()).orElse(ZERO)))
                                    .reduce(ZERO, BigDecimal::add);

                            BigDecimal finalTotalForPeriod = salesList.getTotalFinalAmount();

                            BigDecimal totalDueAmount = salesRecords.stream()
                                    .map(dto -> Optional.ofNullable(dto.getDueAmount()).orElse(ZERO))
                                    .reduce(ZERO, BigDecimal::add);

                            Platform.runLater(() -> {
                                salesTable.setItems(FXCollections.observableArrayList(fxList));
                                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

                                totalSalesAmountLabel.setText(currencyFormat.format(totalNetAmount));
                                totalReturnedAmountLabel.setText(currencyFormat.format(totalReturnedAmount.abs()));
                                finalAmountLabel.setText(currencyFormat.format(finalTotalForPeriod));
                                totalOldGoldLabel.setText(currencyFormat.format(totalOldGoldAmount));
                                totalDueAmountLabel.setText(currencyFormat.format(totalDueAmount));
                            });
                        } catch (IOException e) {
                            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Data Error", "Failed to parse data: " + e.getMessage()));
                        }
                    } else {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "API Error", "Server returned status: " + response.statusCode()));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Network Error", "Could not connect to the server: " + ex.getMessage()));
                    return null;
                });
    }

    private void addActionsToTable() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final HBox pane = new HBox(5, viewButton, editButton);
            {
                pane.setAlignment(Pos.CENTER);
                viewButton.setOnAction(event -> {
                    SalesRecordFX record = getTableView().getItems().get(getIndex());
                    showDetailView(record.getId());
                });
                editButton.setOnAction(event -> {
                    SalesRecordFX record = getTableView().getItems().get(getIndex());
                    showEditView(record.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void showDetailView(Long invoiceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SalesDetailView.fxml"));
            Parent root = loader.load();
            SalesDetailController controller = loader.getController();
            controller.loadInvoiceData(invoiceId);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Invoice Details - " + invoiceId);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open the invoice details view.");
        }
    }

    private void showEditView(Long invoiceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SalesEditView.fxml"));
            Parent root = loader.load();
            SalesEditController controller = loader.getController();
            controller.loadInvoiceData(invoiceId);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Invoice - " + invoiceId);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                loadSalesData();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open the invoice edit view.");
        }
    }

    private SalesRecordFX dtoToFx(SalesRecordDto dto) {
        BigDecimal finalAmountForDisplay = Optional.ofNullable(dto.getFinalAmount()).orElse(ZERO);

        SalesRecordFX fx = new SalesRecordFX(
                dto.getId(),
                dto.getInvoiceNo(),
                dto.getInvoiceDate(),
                dto.getCustomerName(),
                finalAmountForDisplay
        );
        fx.setOldGoldValue(dto.getOldGoldValue());
        fx.setDueAmount(dto.getDueAmount());
        return fx;
    }

    @FXML
    private void handleDownload() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String url = String.format("%s/sales/download?startDate=%s&endDate=%s&status=Paid%%2CPartially%%20Returned", API_BASE_URL, startDate, endDate);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> saveExcelFile(response.body(), startDate, endDate));
                    } else {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Download Failed", "Server responded with error code: " + response.statusCode()));
                    }
                }).exceptionally(ex -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Network Error", "Could not download file: " + ex.getMessage()));
                    return null;
                });
    }

    private void saveExcelFile(byte[] excelData, LocalDate start, LocalDate end) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sales Report");
        fileChooser.setInitialFileName(String.format("sales_data_%s_to_%s.xlsx", start, end));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
        File file = fileChooser.showSaveDialog(salesTable.getScene().getWindow());
        if (file != null) {
            try {
                Files.write(file.toPath(), excelData);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Report saved successfully!");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save the file: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Sales Audit");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}