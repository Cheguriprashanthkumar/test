package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.nishgrid.clienterp.dto.OldGoldExchangeItemDTO;
import org.nishgrid.clienterp.service.ApiService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OldGoldExchangeDetailController {

    @FXML private Label billNoLabel, purchaseDateLabel, sellerNameLabel, sellerMobileLabel;
    @FXML private Label totalPurchaseLabel, processingFeeLabel, netPayableLabel, statusLabel;
    @FXML private TextField paymentAmountField;
    @FXML private ComboBox<String> paymentModeBox;
    @FXML private Button settleButton;
    @FXML private TableView<OldGoldExchangeItemDTO> itemsTable;
    @FXML private TableColumn<OldGoldExchangeItemDTO, String> itemNameCol;
    @FXML private TableColumn<OldGoldExchangeItemDTO, String> metalTypeCol;
    @FXML private TableColumn<OldGoldExchangeItemDTO, Double> netWeightCol;
    @FXML private TableColumn<OldGoldExchangeItemDTO, BigDecimal> wastageCol;
    @FXML private TableColumn<OldGoldExchangeItemDTO, BigDecimal> totalValueCol;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private String currentBillNo;
    private BigDecimal netPayableAmount = BigDecimal.ZERO;
    private static final int DECIMAL_SCALE = 2;

    public void loadPurchaseData(String billNo) {
        this.currentBillNo = billNo;
        String url = ApiService.getBaseUrl() + "/old-gold-exchange/" + billNo;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            Map<String, Object> detailData = objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
                            Platform.runLater(() -> populateView(detailData));
                        } catch (IOException e) {
                            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Data Error", "Failed to parse purchase entity."));
                        }
                    } else {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "API Error", "Bill not found: " + billNo));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Network Error", "Failed to connect to the server."));
                    return null;
                });
    }

    private void populateView(Map<String, Object> entityData) {
        billNoLabel.setText((String) entityData.get("purchaseBillNo"));
        purchaseDateLabel.setText((String) entityData.get("purchaseDate"));
        sellerNameLabel.setText((String) entityData.get("sellerName"));
        sellerMobileLabel.setText((String) entityData.get("sellerMobile"));

        BigDecimal totalPurchase = convertToBigDecimal(entityData.get("totalPurchaseValue")).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal processingFee = convertToBigDecimal(entityData.get("processingFeeAmount")).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
        String status = (String) entityData.get("status");

        netPayableAmount = convertToBigDecimal(entityData.get("netPayableAmount")).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        totalPurchaseLabel.setText(currencyFormat.format(totalPurchase));
        processingFeeLabel.setText(currencyFormat.format(processingFee));
        netPayableLabel.setText(currencyFormat.format(netPayableAmount));
        paymentAmountField.setText(netPayableAmount.toPlainString());

        updateStatusDisplay(status);
        setupTable();

        List<Map<String, Object>> itemMaps = (List<Map<String, Object>>) entityData.get("items");
        List<OldGoldExchangeItemDTO> itemDTOs = itemMaps.stream()
                .map(this::mapMapToItemDto)
                .collect(Collectors.toList());
        itemsTable.setItems(FXCollections.observableArrayList(itemDTOs));

        if ("PENDING_PAYOUT".equals(status) || "PARTIALLY_PAID".equals(status)) {
            settleButton.setDisable(false);
        } else {
            settleButton.setDisable(true);
        }
    }

    private OldGoldExchangeItemDTO mapMapToItemDto(Map<String, Object> itemMap) {
        OldGoldExchangeItemDTO dto = new OldGoldExchangeItemDTO();
        dto.setItemName((String) itemMap.get("itemName"));
        dto.setMetalType((String) itemMap.get("metalType"));
        dto.setNetWeight(convertToDouble(itemMap.get("netWeight")));
        dto.setWastagePercent(convertToBigDecimal(itemMap.get("wastagePercent")));
        dto.setTotalItemValue(convertToBigDecimal(itemMap.get("totalItemValue")));
        return dto;
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return new BigDecimal(value.toString());
        if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    private Double convertToDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        return 0.0;
    }

    private void setupTable() {
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        metalTypeCol.setCellValueFactory(new PropertyValueFactory<>("metalType"));
        netWeightCol.setCellValueFactory(new PropertyValueFactory<>("netWeight"));
        wastageCol.setCellValueFactory(new PropertyValueFactory<>("wastagePercent"));
        totalValueCol.setCellValueFactory(new PropertyValueFactory<>("totalItemValue"));
    }

    @FXML
    private void handleSettlePayment() {
        try {
            BigDecimal amount = new BigDecimal(paymentAmountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Payment amount must be positive.");
                return;
            }
            if (amount.compareTo(netPayableAmount) > 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Payment amount exceeds net payable amount.");
                return;
            }

            sendPaymentRequest(amount);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the amount.");
        }
    }

    private void sendPaymentRequest(BigDecimal amount) {
        String url = ApiService.getBaseUrl() + "/old-gold-exchange/" + currentBillNo + "/payments";
        String requestBody = "{\"amountPaid\":" + amount.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP).toPlainString() + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        try {
                            Map<String, Object> responseMap = objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
                            String newStatus = (String) responseMap.get("status");
                            updateStatusDisplay(newStatus);
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment settled successfully!");
                            if ("PAID".equals(newStatus)) {
                                settleButton.setDisable(true);
                            }
                        } catch (IOException e) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Payment recorded, but failed to parse response.");
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Payment Failed", "Server error during settlement.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Network Error", "Could not send payment request."));
                    return null;
                });
    }

    private void updateStatusDisplay(String status) {
        statusLabel.setText("Status: " + status);
        if ("PENDING_PAYOUT".equals(status) || "PARTIALLY_PAID".equals(status)) {
            statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: orange;");
            settleButton.setDisable(false);
        } else if ("PAID".equals(status)) {
            statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: green;");
            settleButton.setDisable(true);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) billNoLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Purchase Settlement");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}