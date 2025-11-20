package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.nishgrid.clienterp.model.SalesSummaryReport;
import org.nishgrid.clienterp.service.ApiService;
import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DashboardContentController {

    @FXML private VBox rootVBox;
    @FXML private ComboBox<String> financialYearCombo;
    @FXML private Label financialYearStatus;
    @FXML private TableView<SalesSummaryReport> todaySalesTable;
    @FXML private TableColumn<SalesSummaryReport, LocalDate> salesDateCol;
    @FXML private TableColumn<SalesSummaryReport, BigDecimal> salesTotalCol;
    @FXML private TableView<SalesSummaryReport> monthlyProfitTable;
    @FXML private TableColumn<SalesSummaryReport, LocalDate> monthlyFromCol;
    @FXML private TableColumn<SalesSummaryReport, LocalDate> monthlyToCol;
    @FXML private TableColumn<SalesSummaryReport, BigDecimal> monthlyProfitCol;
    @FXML private TableView<SalesSummaryReport> yearlyProfitTable;
    @FXML private TableColumn<SalesSummaryReport, String> yearlyYearCol;
    @FXML private TableColumn<SalesSummaryReport, BigDecimal> yearlyProfitCol;

    private int clientStartYear = 2023;
    private final String BASE_URL = ApiService.getBaseUrl()+ "/reports";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @FXML
    public void initialize() {
        populateFinancialYears();
        setupTableColumns();
        loadAllDashboardData();
    }

    private void loadAllDashboardData() {
        loadTodayReport();
        loadFinancialYearData();
    }

    private void populateFinancialYears() {
        int endYear = LocalDate.now().getMonthValue() < 4 ? LocalDate.now().getYear() - 1 : LocalDate.now().getYear();
        financialYearCombo.getItems().clear();
        for (int year = clientStartYear; year <= endYear; year++) {
            String fy = String.format("%d-%02d", year, (year + 1) % 100);
            financialYearCombo.getItems().add(fy);
        }
        if (!financialYearCombo.getItems().isEmpty()) {
            financialYearCombo.getSelectionModel().selectLast();
            updateFinancialYearStatus();
        }
    }

    @FXML
    private void handleFinancialYearChange() {
        updateFinancialYearStatus();
        loadFinancialYearData();
    }

    private void updateFinancialYearStatus() {
        String selected = financialYearCombo.getValue();
        if (selected != null) {
            financialYearStatus.setText("Active Year: " + selected);
        }
    }

    private void setupTableColumns() {
        salesDateCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getReportDate()));
        salesTotalCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalSales()));
        formatCurrencyColumn(salesTotalCol);

        monthlyFromCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getReportDate()));
        monthlyToCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(YearMonth.from(cell.getValue().getReportDate()).atEndOfMonth()));
        monthlyProfitCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalSales()));
        formatCurrencyColumn(monthlyProfitCol);

        yearlyYearCol.setCellValueFactory(cell -> new SimpleStringProperty(financialYearCombo.getValue()));
        yearlyProfitCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalSales()));
        formatCurrencyColumn(yearlyProfitCol);
    }

    private <T> void formatCurrencyColumn(TableColumn<SalesSummaryReport, T> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f", (BigDecimal) item));
                }
            }
        });
    }

    private void loadTodayReport() {
        generateReportAsync("DAILY", LocalDate.now(), null)
                .thenAccept(report -> Platform.runLater(() -> {
                    if (report != null) {
                        todaySalesTable.setItems(FXCollections.observableArrayList(report));
                    } else {
                        todaySalesTable.setItems(FXCollections.emptyObservableList());
                    }
                })).exceptionally(ex -> handleApiError("Today's Sales", ex));
    }

    private void loadFinancialYearData() {
        String selectedFY = financialYearCombo.getValue();
        if (selectedFY == null) return;
        int startYear = Integer.parseInt(selectedFY.substring(0, 4));

        loadMonthlyBreakdown(startYear);
        loadYearlySummary(startYear);
    }

    private void loadMonthlyBreakdown(int startYear) {
        List<CompletableFuture<SalesSummaryReport>> monthlyFutures = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LocalDate monthDate = LocalDate.of(startYear, 4, 1).plusMonths(i);
            monthlyFutures.add(generateReportAsync("MONTHLY", monthDate, null));
        }

        CompletableFuture.allOf(monthlyFutures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {
                    List<SalesSummaryReport> monthlyReports = monthlyFutures.stream()
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    Platform.runLater(() -> monthlyProfitTable.setItems(FXCollections.observableArrayList(monthlyReports)));
                }).exceptionally(ex -> handleApiError("Monthly Breakdown", ex));
    }

    private void loadYearlySummary(int startYear) {
        generateReportAsync("FINANCIAL_YEAR", LocalDate.of(startYear, 4, 1), null)
                .thenAccept(report -> Platform.runLater(() -> {
                    if (report != null) {
                        yearlyProfitTable.setItems(FXCollections.observableArrayList(report));
                    } else {
                        yearlyProfitTable.setItems(FXCollections.emptyObservableList());
                    }
                })).exceptionally(ex -> handleApiError("Yearly Summary", ex));
    }

    private CompletableFuture<SalesSummaryReport> generateReportAsync(String reportType, LocalDate startDate, LocalDate endDate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> payload = new HashMap<>();
                payload.put("reportType", reportType);
                payload.put("startDate", startDate.toString());
                if (endDate != null) payload.put("endDate", endDate.toString());
                String jsonPayload = objectMapper.writeValueAsString(payload);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/generate"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200 && !response.body().isEmpty()) {
                    return objectMapper.readValue(response.body(), SalesSummaryReport.class);
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private Void handleApiError(String context, Throwable ex) {
        System.err.println("Failed to load data for: " + context);
        ex.printStackTrace();
        return null;
    }

    @FXML
    private void handleChangePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Background Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Window stage = rootVBox.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            BackgroundImage bg = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false));
            rootVBox.setBackground(new Background(bg));
        }
    }
}