package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.nishgrid.clienterp.dto.SalesReportDTO;
import org.nishgrid.clienterp.model.SalesReport;
import org.nishgrid.clienterp.service.ApiService;
import org.nishgrid.clienterp.util.DateUtils;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SalesreportController {

    @FXML private ComboBox<String> reportTypeFilter;
    @FXML private ComboBox<String> generateTypeBox;
    @FXML private DatePicker generateDatePicker;
    @FXML private DatePicker generateEndDatePicker;
    @FXML private Label endDateLabel;
    @FXML private TableView<SalesReport> reportTable;
    @FXML private TableColumn<SalesReport, Long> idCol;
    @FXML private TableColumn<SalesReport, String> dateCol, typeCol, generatedOnCol;
    @FXML private TableColumn<SalesReport, BigDecimal> salesCol, discountCol;
    @FXML private TableColumn<SalesReport, Integer> itemsCol, customersCol;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_BASE_URL = ApiService.getBaseUrl() + "/reports";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        DateUtils.restrictFutureDates(generateDatePicker);
        DateUtils.restrictFutureDates(generateEndDatePicker);
        generateDatePicker.setValue(LocalDate.now());

        generateTypeBox.setItems(FXCollections.observableArrayList(
                "DAILY", "MONTHLY", "YEARLY", "FINANCIAL_YEAR", "CUSTOM"));
        generateTypeBox.setValue("DAILY");

        reportTypeFilter.setItems(FXCollections.observableArrayList(
                "ALL", "DAILY", "MONTHLY", "YEARLY", "FINANCIAL_YEAR", "CUSTOM"));
        reportTypeFilter.setValue("ALL");

        setupTableColumns();
        setupDynamicControls();

        reportTypeFilter.setOnAction(e -> loadReports());
        loadReports();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(data -> data.getValue().reportIdProperty().asObject());
        dateCol.setCellValueFactory(data -> data.getValue().reportDateProperty());
        typeCol.setCellValueFactory(data -> data.getValue().reportTypeProperty());
        salesCol.setCellValueFactory(data -> data.getValue().totalSalesProperty());
        discountCol.setCellValueFactory(data -> data.getValue().totalDiscountProperty());
        itemsCol.setCellValueFactory(data -> data.getValue().totalItemsSoldProperty().asObject());
        customersCol.setCellValueFactory(data -> data.getValue().totalCustomersProperty().asObject());
        generatedOnCol.setCellValueFactory(data -> data.getValue().generatedOnProperty());

        formatCurrencyColumn(salesCol);
        formatCurrencyColumn(discountCol);
    }

    private void formatCurrencyColumn(TableColumn<SalesReport, BigDecimal> column) {
        column.setCellFactory(col -> new TableCell<SalesReport, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f", item));
                }
            }
        });
    }

    private void setupDynamicControls() {
        endDateLabel.setVisible(false);
        generateEndDatePicker.setVisible(false);
        generateTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCustom = "CUSTOM".equals(newVal);
            endDateLabel.setVisible(isCustom);
            generateEndDatePicker.setVisible(isCustom);
        });
    }

    private void loadReports() {
        String type = reportTypeFilter.getValue();
        String url = API_BASE_URL;
        if (type != null && !"ALL".equals(type)) {
            url += "?type=" + type;
        }

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() == 200) {
                try {
                    List<SalesReportDTO> dtoList = objectMapper.readValue(response.body(), new TypeReference<>() {});
                    List<SalesReport> fxList = dtoList.stream().map(this::dtoToFx).collect(Collectors.toList());
                    Platform.runLater(() -> reportTable.setItems(FXCollections.observableArrayList(fxList)));
                } catch (Exception e) {
                    Platform.runLater(() -> showAlert("Error parsing reports: " + e.getMessage()));
                }
            } else {
                Platform.runLater(() -> showAlert("Failed to load reports. Code: " + response.statusCode()));
            }
        }).exceptionally(ex -> {
            Platform.runLater(() -> showAlert("Network error: " + ex.getMessage()));
            return null;
        });
    }

    @FXML
    private void handleGenerateReport() {
        String reportType = generateTypeBox.getValue();
        LocalDate startDate = generateDatePicker.getValue();
        LocalDate endDate = generateEndDatePicker.getValue();

        if (startDate == null || reportType == null) {
            showAlert("Please select a report type and a start date.");
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("reportType", reportType);
        payload.put("startDate", startDate.toString());

        if ("CUSTOM".equals(reportType)) {
            if (endDate == null) {
                showAlert("Please select an end date for the custom range.");
                return;
            }
            payload.put("endDate", endDate.toString());
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
                if (response.statusCode() == 200) {
                    Platform.runLater(() -> {
                        showAlert(reportType + " report generated successfully!");
                        loadReports();
                    });
                } else {
                    Platform.runLater(() -> showAlert("Failed to generate report. Code: " + response.statusCode()));
                }
            }).exceptionally(ex -> {
                Platform.runLater(() -> showAlert("Network error: " + ex.getMessage()));
                return null;
            });

        } catch (Exception e) {
            showAlert("Error creating request: " + e.getMessage());
        }
    }

    @FXML
    private void handleDownload() {
        SalesReport selected = reportTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a report to download.");
            return;
        }

        String url = API_BASE_URL + "/" + selected.getReportId() + "/download";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(response -> {
            if (response.statusCode() == 200) {
                Platform.runLater(() -> saveExcelFile(response.body(), selected.getReportId()));
            } else {
                Platform.runLater(() -> showAlert("Failed to download report. Code: " + response.statusCode()));
            }
        }).exceptionally(ex -> {
            Platform.runLater(() -> showAlert("Network error: " + ex.getMessage()));
            return null;
        });
    }

    private void saveExcelFile(byte[] excelData, long reportId) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.setInitialFileName("sales_report_" + reportId + ".xlsx");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
        File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());

        if (file != null) {
            try {
                Files.write(file.toPath(), excelData);
                showAlert("Report saved successfully!");
            } catch (Exception e) {
                showAlert("Error saving file: " + e.getMessage());
            }
        }
    }

    private SalesReport dtoToFx(SalesReportDTO dto) {
        return new SalesReport(
                dto.getReportId(),
                dto.getReportDate().toString(),
                dto.getReportType(),
                dto.getTotalSales(),
                dto.getTotalDiscount(), // Removed dto.getTotalTax()
                dto.getTotalItemsSold(),
                dto.getTotalCustomers(),
                dto.getGeneratedOn().format(formatter)
        );
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}