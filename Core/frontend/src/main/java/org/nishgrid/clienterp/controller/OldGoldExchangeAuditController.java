package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.dto.OldGoldExchangeRequestDTO;
import org.nishgrid.clienterp.service.ApiService;
import org.nishgrid.clienterp.util.DateUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class OldGoldExchangeAuditController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> statusFilterBox;
    @FXML private TableView<OldGoldExchangeRequestDTO> purchaseTable;
    @FXML private TableColumn<OldGoldExchangeRequestDTO, String> billNoCol;
    @FXML private TableColumn<OldGoldExchangeRequestDTO, LocalDate> purchaseDateCol;
    @FXML private TableColumn<OldGoldExchangeRequestDTO, String> sellerNameCol;
    @FXML private TableColumn<OldGoldExchangeRequestDTO, BigDecimal> totalPurchaseValueCol;
    @FXML private TableColumn<OldGoldExchangeRequestDTO, BigDecimal> netPayableAmountCol;
    @FXML private TableColumn<OldGoldExchangeRequestDTO, String> statusCol;
    @FXML private TableColumn<OldGoldExchangeRequestDTO, Void> actionsCol;
    @FXML private Label totalPurchaseLabel;
    @FXML private Label totalNetPayableLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_BASE_URL = ApiService.getBaseUrl() + "/old-gold-exchange";
    private final BigDecimal ZERO = BigDecimal.ZERO;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final int DECIMAL_SCALE = 2;

    @FXML
    public void initialize() {
        DateUtils.restrictFutureDates(startDatePicker);
        DateUtils.restrictFutureDates(endDatePicker);
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        setupFilters();
        setupTableColumns();
        addActionsToTable();
        loadPurchaseData();
    }

    private void setupFilters() {
        statusFilterBox.setItems(FXCollections.observableArrayList("All", "PENDING_PAYOUT", "PAID", "PARTIALLY_PAID"));
        statusFilterBox.getSelectionModel().select("All");
        statusFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> handleFilter());
    }

    private void setupTableColumns() {
        billNoCol.setCellValueFactory(new PropertyValueFactory<>("purchaseBillNo"));
        purchaseDateCol.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        sellerNameCol.setCellValueFactory(new PropertyValueFactory<>("sellerName"));
        totalPurchaseValueCol.setCellValueFactory(new PropertyValueFactory<>("totalPurchaseValue"));
        netPayableAmountCol.setCellValueFactory(new PropertyValueFactory<>("netPayableAmount"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalPurchaseValueCol.setCellFactory(this::createCurrencyCell);
        netPayableAmountCol.setCellFactory(this::createCurrencyCell);
    }

    private <T> TableCell<T, BigDecimal> createCurrencyCell(TableColumn<T, BigDecimal> column) {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : currencyFormat.format(value.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP)));
            }
        };
    }

    @FXML
    private void handleFilter() {
        loadPurchaseData();
    }

    private void loadPurchaseData() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String status = statusFilterBox.getValue();

        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date Range", "Please select both a start and end date.");
            return;
        }

        String encodedStatus = "All".equalsIgnoreCase(status) ? "" : URLEncoder.encode(status, StandardCharsets.UTF_8);
        String url = String.format("%s?startDate=%s&endDate=%s&status=%s", API_BASE_URL, startDate, endDate, encodedStatus);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<OldGoldExchangeRequestDTO> purchaseList = objectMapper.readValue(response.body(), new TypeReference<List<OldGoldExchangeRequestDTO>>() {});
                            BigDecimal totalValue = purchaseList.stream()
                                    .map(dto -> Optional.ofNullable(dto.getTotalPurchaseValue()).orElse(ZERO))
                                    .reduce(ZERO, BigDecimal::add);

                            BigDecimal totalNetPayable = purchaseList.stream()
                                    .map(dto -> Optional.ofNullable(dto.getNetPayableAmount()).orElse(ZERO))
                                    .reduce(ZERO, BigDecimal::add);

                            Platform.runLater(() -> {
                                purchaseTable.setItems(FXCollections.observableArrayList(purchaseList));
                                totalPurchaseLabel.setText(currencyFormat.format(totalValue.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP)));
                                totalNetPayableLabel.setText(currencyFormat.format(totalNetPayable.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP)));
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
            private final Button viewButton = new Button("View/Pay");
            private final HBox pane = new HBox(5, viewButton);
            {
                pane.setAlignment(Pos.CENTER);
                viewButton.setOnAction(event -> {
                    OldGoldExchangeRequestDTO record = getTableView().getItems().get(getIndex());
                    showDetailView(record.getPurchaseBillNo());
                    loadPurchaseData();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void showDetailView(String billNo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OldGoldExchangeDetailView.fxml"));
            Parent root = loader.load();
            OldGoldExchangeDetailController controller = loader.getController();
            controller.loadPurchaseData(billNo);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Purchase Details - " + billNo);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open the purchase details view.");
        }
    }

    @FXML
    private void handleDownload() {
        showAlert(Alert.AlertType.INFORMATION, "Download Feature", "Download report feature is not yet implemented.");
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Purchase Audit");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}