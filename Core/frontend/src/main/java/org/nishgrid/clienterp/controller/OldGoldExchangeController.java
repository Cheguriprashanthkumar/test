package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.OldGoldExchangeRequestDTO;
import org.nishgrid.clienterp.dto.OldGoldExchangeItemDTO;
import org.nishgrid.clienterp.service.ApiService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class OldGoldExchangeController {

    @FXML private TextField sellerNameField;
    @FXML private TextField sellerMobileField;
    @FXML private TextArea sellerAddressField;
    @FXML private TextField purchaseBillNoField;
    @FXML private DatePicker purchaseDatePicker;
    @FXML private ComboBox<String> payoutModeBox;
    @FXML private TextField otherPayoutModeField;
    @FXML private TextArea remarksField;

    @FXML private TextField itemNameField;
    @FXML private ComboBox<String> metalTypeBox;
    @FXML private TextField purityField;
    @FXML private TextField grossWeightField;
    @FXML private TextField wastagePercentField;
    @FXML private TextField netWeightField;
    @FXML private TextField ratePerGramField;
    @FXML private TextField diamondCaratField;
    @FXML private TextField diamondRateField;
    @FXML private TextField deductionChargeField;
    @FXML private TextField totalItemValueField;

    @FXML private TableView<OldGoldExchangeItemDTO> purchaseItemTableView;
    @FXML private TableColumn<OldGoldExchangeItemDTO, String> itemNameColumn;
    @FXML private TableColumn<OldGoldExchangeItemDTO, String> metalTypeColumn;
    @FXML private TableColumn<OldGoldExchangeItemDTO, String> purityColumn;
    @FXML private TableColumn<OldGoldExchangeItemDTO, Double> grossWeightColumn;
    @FXML private TableColumn<OldGoldExchangeItemDTO, Double> netWeightColumn;
    @FXML private TableColumn<OldGoldExchangeItemDTO, BigDecimal> wastagePercentColumn;
    @FXML private TableColumn<OldGoldExchangeItemDTO, BigDecimal> ratePerGramColumn;
    @FXML private TableColumn<OldGoldExchangeItemDTO, BigDecimal> diamondCaratColumn;
    @FXML private TableColumn<OldGoldExchangeItemDTO, BigDecimal> deductionChargeColumn;

    @FXML private TextField totalPurchaseValueField;
    @FXML private TextField processingFeePercentField;
    @FXML private TextField processingFeeAmountField;
    @FXML private TextField netPayableAmountField;

    private final ObservableList<OldGoldExchangeItemDTO> purchaseItemsData = FXCollections.observableArrayList();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final HttpClient client = HttpClient.newHttpClient();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private OldGoldExchangeItemDTO selectedItem = null;
    private boolean isUpdatingFeeFields = false;
    private static final int DECIMAL_SCALE = 2;

    @FXML
    public void initialize() {
        setupPurchaseItemTable();
        addCalculationListeners();
        setupInputRestrictions();
        setupSequentialNavigation();
        setupValidation();
        fetchAndSetNextBillNumber();
        purchaseDatePicker.setValue(LocalDate.now());

        purchaseItemTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                clearItemInputs();
            } else {
                selectedItem = newSelection;
                populateItemFields(newSelection);
            }
        });
    }

    private void setupInputRestrictions() {
        UnaryOperator<TextFormatter.Change> mobileFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d{0,10}") ? change : null;
        };
        sellerMobileField.setTextFormatter(new TextFormatter<>(mobileFilter));
    }

    private void setupSequentialNavigation() {
        List<Control> controls = Stream.of(
                sellerNameField, sellerMobileField, sellerAddressField,
                purchaseDatePicker.getEditor(), payoutModeBox, otherPayoutModeField, remarksField,
                itemNameField, metalTypeBox, purityField, grossWeightField, wastagePercentField, ratePerGramField,
                diamondCaratField, diamondRateField, deductionChargeField
        ).collect(ArrayList::new, (list, item) -> {
            if (item instanceof TextField) {
                list.add(item);
            } else if (item instanceof DatePicker) {
                list.add(item);
            } else if (item instanceof ComboBox) {
                list.add(item);
            }
            if (item instanceof TextArea) {
                list.add(item);
            }
        }, ArrayList::addAll);

        for (int i = 0; i < controls.size() - 1; i++) {
            final Control currentControl = controls.get(i);
            final Control nextControl = controls.get(i + 1);

            currentControl.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    nextControl.requestFocus();
                    event.consume();
                }
            });
        }
    }

    private void addCalculationListeners() {
        grossWeightField.textProperty().addListener((obs, oldV, newV) -> { calculateNetWeight(); updateItemTotalValue(); });
        wastagePercentField.textProperty().addListener((obs, oldV, newV) -> { calculateNetWeight(); updateItemTotalValue(); });
        ratePerGramField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalValue());
        diamondCaratField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalValue());
        diamondRateField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalValue());
        deductionChargeField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalValue());

        purchaseItemsData.addListener((ListChangeListener<OldGoldExchangeItemDTO>) c -> updatePurchaseTotals());

        processingFeePercentField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdatingFeeFields) return;
            isUpdatingFeeFields = true;
            BigDecimal totalValue = calculateTotalPurchaseValue();
            BigDecimal feePercent = parseBigDecimalSafe(newVal);
            BigDecimal feeAmount = totalValue.multiply(feePercent).divide(new BigDecimal("100"), DECIMAL_SCALE, RoundingMode.HALF_UP);
            processingFeeAmountField.setText(feeAmount.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP).toPlainString());
            isUpdatingFeeFields = false;
            updatePurchaseTotals();
        });

        processingFeeAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdatingFeeFields) return;
            isUpdatingFeeFields = true;
            BigDecimal totalValue = calculateTotalPurchaseValue();
            BigDecimal feeAmount = parseBigDecimalSafe(newVal);
            if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percent = feeAmount.multiply(new BigDecimal("100")).divide(totalValue, DECIMAL_SCALE, RoundingMode.HALF_UP);
                processingFeePercentField.setText(percent.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP).toPlainString());
            } else {
                processingFeePercentField.setText("0.00");
            }
            isUpdatingFeeFields = false;
            updatePurchaseTotals();
        });
    }

    private void calculateNetWeight() {
        BigDecimal grossWeight = parseBigDecimalSafe(grossWeightField.getText());
        BigDecimal wastagePercent = parseBigDecimalSafe(wastagePercentField.getText());

        if (wastagePercent.compareTo(new BigDecimal("100")) > 0) {
            showErrorAlert("Wastage percentage cannot exceed 100%.");
            return;
        }

        BigDecimal wastageAmount = grossWeight.multiply(wastagePercent).divide(new BigDecimal("100"), 3, RoundingMode.HALF_UP);
        BigDecimal netWeightDecimal = grossWeight.subtract(wastageAmount).setScale(3, RoundingMode.HALF_UP);
        netWeightField.setText(netWeightDecimal.toPlainString());
    }

    private void updateItemTotalValue() {
        BigDecimal grossWeight = parseBigDecimalSafe(grossWeightField.getText());
        BigDecimal wastagePercent = parseBigDecimalSafe(wastagePercentField.getText());
        BigDecimal rate = parseBigDecimalSafe(ratePerGramField.getText());
        BigDecimal diamondCarat = parseBigDecimalSafe(diamondCaratField.getText());
        BigDecimal diamondRate = parseBigDecimalSafe(diamondRateField.getText());
        BigDecimal deductionCharge = parseBigDecimalSafe(deductionChargeField.getText());

        BigDecimal wastageAmount = grossWeight.multiply(wastagePercent).divide(new BigDecimal("100"), 3, RoundingMode.HALF_UP);
        BigDecimal netWeightDecimal = grossWeight.subtract(wastageAmount);

        BigDecimal metalValue = netWeightDecimal.multiply(rate);
        BigDecimal diamondValue = diamondCarat.multiply(diamondRate);

        BigDecimal totalValue = metalValue
                .add(diamondValue)
                .subtract(deductionCharge)
                .setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);

        totalItemValueField.setText(totalValue.toPlainString());
    }

    private BigDecimal calculateTotalPurchaseValue() {
        return purchaseItemsData.stream()
                .map(item -> Optional.ofNullable(item.getTotalItemValue()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updatePurchaseTotals() {
        BigDecimal totalValue = calculateTotalPurchaseValue();
        totalPurchaseValueField.setText(totalValue.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP).toPlainString());

        BigDecimal feeAmount = parseBigDecimalSafe(processingFeeAmountField.getText());
        BigDecimal netPayable = totalValue.subtract(feeAmount);
        netPayableAmountField.setText(netPayable.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP).toPlainString());
    }

    @FXML
    private void handleAddItem() {
        if (!validateItemInputs()) {
            return;
        }

        OldGoldExchangeItemDTO item = (selectedItem != null) ? selectedItem : new OldGoldExchangeItemDTO();
        item.setItemName(itemNameField.getText());
        item.setMetalType(metalTypeBox.getValue());
        item.setPurity(purityField.getText());
        item.setGrossWeight(parseDoubleSafe(grossWeightField.getText()));
        item.setWastagePercent(parseBigDecimalSafe(wastagePercentField.getText()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP));
        item.setNetWeight(parseDoubleSafe(netWeightField.getText()));
        item.setRatePerGram(parseBigDecimalSafe(ratePerGramField.getText()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP));
        item.setDiamondCarat(parseBigDecimalSafe(diamondCaratField.getText()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP));
        item.setDiamondRate(parseBigDecimalSafe(diamondRateField.getText()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP));
        item.setDeductionCharge(parseBigDecimalSafe(deductionChargeField.getText()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP));
        item.setTotalItemValue(parseBigDecimalSafe(totalItemValueField.getText()).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP));

        if (selectedItem == null) {
            purchaseItemsData.add(item);
        } else {
            purchaseItemTableView.refresh();
            selectedItem = null;
        }
        clearItemInputs();
    }

    @FXML
    private void handleEditSelectedItem() {
        OldGoldExchangeItemDTO selected = purchaseItemTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to edit.");
            return;
        }
        selectedItem = selected;
        populateItemFields(selected);
    }

    @FXML
    private void handleDeleteItem() {
        OldGoldExchangeItemDTO selected = purchaseItemTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            purchaseItemsData.remove(selected);
            clearItemInputs();
        } else {
            showAlert(AlertType.WARNING, "No Selection", "Please select an item to delete.");
        }
    }

    @FXML
    private void handleSubmit() {
        if (!validateOverallForm()) return;

        try {
            OldGoldExchangeRequestDTO dto = new OldGoldExchangeRequestDTO();
            dto.setSellerName(sellerNameField.getText());
            dto.setSellerMobile(sellerMobileField.getText());
            dto.setSellerAddress(sellerAddressField.getText());
            dto.setPurchaseDate(purchaseDatePicker.getValue());
            dto.setPayoutMode(payoutModeBox.getValue());
            if ("Other".equals(payoutModeBox.getValue())) {
                dto.setOtherPayoutMode(otherPayoutModeField.getText());
            }
            dto.setRemarks(remarksField.getText());
            dto.setProcessingFeePercent(parseBigDecimalSafe(processingFeePercentField.getText()));
            dto.setItems(new ArrayList<>(purchaseItemsData));
            dto.setPurchaseBillNo(purchaseBillNoField.getText());

            String json = mapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiService.getBaseUrl() + "/old-gold-exchange"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> Platform.runLater(() -> {
                        Map<String, Object> responseMap;
                        try {
                            responseMap = mapper.readValue(response.body(), Map.class);
                        } catch (Exception e) {
                            showErrorAlert("API Error (" + response.statusCode() + "): Could not parse response body.");
                            return;
                        }

                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            String billNo = (String) responseMap.get("purchaseBillNo");
                            BigDecimal netPayable = parseBigDecimalSafe(String.valueOf(responseMap.get("netPayableAmount")));
                            showAlert(AlertType.INFORMATION, "Success", "Purchase Bill created successfully!\nBill No: " + billNo + "\nNet Payable: " + netPayable.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP).toPlainString());
                            resetForm();
                        } else {
                            String errorMessage = (String) responseMap.getOrDefault("message", "Unknown error");
                            showErrorAlert("API Error (" + response.statusCode() + "): " + errorMessage);
                        }
                    }))
                    .exceptionally(e -> {
                        Platform.runLater(() -> showErrorAlert("Connection error: " + e.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Unexpected client error: " + e.getMessage());
        }
    }

    private boolean validateItemInputs() {
        BigDecimal totalValue = parseBigDecimalSafe(totalItemValueField.getText());
        if (itemNameField.getText().isBlank()) {
            highlightField(itemNameField);
            showErrorAlert("Item Name is required.");
            return false;
        }
        clearHighlight(itemNameField);
        if (metalTypeBox.getValue() == null) {
            highlightField(metalTypeBox);
            showErrorAlert("Metal Type is required.");
            return false;
        }
        clearHighlight(metalTypeBox);
        if (totalValue.compareTo(BigDecimal.ZERO) <= 0) {
            showErrorAlert("Total Item Value must be greater than 0. Check Gross Weight, Rate, and Wastage %.");
            highlightField(grossWeightField);
            highlightField(ratePerGramField);
            return false;
        }
        clearHighlight(grossWeightField);
        clearHighlight(ratePerGramField);
        return true;
    }

    private boolean validateOverallForm() {
        clearHighlight(sellerNameField);
        clearHighlight(sellerMobileField);
        clearHighlight(purchaseDatePicker.getEditor());
        clearHighlight(payoutModeBox);
        clearHighlight(otherPayoutModeField);

        if (sellerNameField.getText().isBlank()) {
            showErrorAlert("Seller name is required.");
            highlightField(sellerNameField);
            return false;
        }
        if (sellerMobileField.getText().length() != 10) {
            showErrorAlert("Mobile number must be 10 digits.");
            highlightField(sellerMobileField);
            return false;
        }
        if (purchaseDatePicker.getValue() == null) {
            showErrorAlert("Purchase date is required.");
            highlightField(purchaseDatePicker.getEditor());
            return false;
        }
        if (payoutModeBox.getValue() == null || payoutModeBox.getValue().isBlank()) {
            showErrorAlert("Select a payout mode.");
            highlightField(payoutModeBox);
            return false;
        }
        if ("Other".equals(payoutModeBox.getValue()) && otherPayoutModeField.getText().isBlank()) {
            showErrorAlert("Specify other payout mode.");
            highlightField(otherPayoutModeField);
            return false;
        }
        if (purchaseItemsData.isEmpty()) {
            showErrorAlert("Add at least one item to the purchase bill.");
            return false;
        }
        return true;
    }

    private void resetForm() {
        sellerNameField.clear();
        sellerMobileField.clear();
        sellerAddressField.clear();
        purchaseDatePicker.setValue(LocalDate.now());
        payoutModeBox.getSelectionModel().clearSelection();
        otherPayoutModeField.clear();
        remarksField.clear();
        processingFeePercentField.setText("0.00");
        processingFeeAmountField.setText("0.00");
        totalPurchaseValueField.setText("0.00");
        netPayableAmountField.setText("0.00");
        purchaseItemsData.clear();
        clearItemInputs();
        fetchAndSetNextBillNumber();
    }

    private void populateItemFields(OldGoldExchangeItemDTO item) {
        itemNameField.setText(item.getItemName());
        metalTypeBox.setValue(item.getMetalType());
        purityField.setText(item.getPurity());
        grossWeightField.setText(item.getGrossWeight() != null ? String.valueOf(item.getGrossWeight()) : "");
        wastagePercentField.setText(item.getWastagePercent() != null ? item.getWastagePercent().toPlainString() : "");
        ratePerGramField.setText(item.getRatePerGram() != null ? item.getRatePerGram().toPlainString() : "");
        diamondCaratField.setText(item.getDiamondCarat() != null ? item.getDiamondCarat().toPlainString() : "");
        diamondRateField.setText(item.getDiamondRate() != null ? item.getDiamondRate().toPlainString() : "");
        deductionChargeField.setText(item.getDeductionCharge() != null ? item.getDeductionCharge().toPlainString() : "");
        calculateNetWeight();
        updateItemTotalValue();
    }

    private void clearItemInputs() {
        itemNameField.clear();
        metalTypeBox.getSelectionModel().clearSelection();
        purityField.clear();
        grossWeightField.clear();
        wastagePercentField.clear();
        netWeightField.clear();
        ratePerGramField.clear();
        diamondCaratField.clear();
        diamondRateField.clear();
        deductionChargeField.clear();
        totalItemValueField.clear();
        purchaseItemTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }

    private void fetchAndSetNextBillNumber() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiService.getBaseUrl() + "/old-gold-exchange/next-bill-number")).header("Content-Type", "application/json").GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() == 200) {
                try {
                    Map<String, String> responseMap = mapper.readValue(response.body(), Map.class);
                    String nextBillNumber = responseMap.get("purchaseBillNo");
                    Platform.runLater(() -> purchaseBillNoField.setText(nextBillNumber));
                } catch (Exception e) {
                    Platform.runLater(() -> purchaseBillNoField.setPromptText("Error fetching bill number"));
                }
            }
        }).exceptionally(e -> {
            Platform.runLater(() -> purchaseBillNoField.setPromptText("Connection Error"));
            return null;
        });
    }

    private double parseDoubleSafe(String text) {
        try { return (text == null || text.isBlank()) ? 0.0 : Double.parseDouble(text.trim()); } catch (NumberFormatException e) { return 0.0; }
    }

    private BigDecimal parseBigDecimalSafe(String text) {
        try {
            if (text == null || text.isBlank()) { return BigDecimal.ZERO; }
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setupPurchaseItemTable() {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        metalTypeColumn.setCellValueFactory(new PropertyValueFactory<>("metalType"));
        purityColumn.setCellValueFactory(new PropertyValueFactory<>("purity"));
        grossWeightColumn.setCellValueFactory(new PropertyValueFactory<>("grossWeight"));
        netWeightColumn.setCellValueFactory(new PropertyValueFactory<>("netWeight"));
        wastagePercentColumn.setCellValueFactory(new PropertyValueFactory<>("wastagePercent"));
        ratePerGramColumn.setCellValueFactory(new PropertyValueFactory<>("ratePerGram"));
        diamondCaratColumn.setCellValueFactory(new PropertyValueFactory<>("diamondCarat"));
        deductionChargeColumn.setCellValueFactory(new PropertyValueFactory<>("deductionCharge"));
        purchaseItemTableView.setItems(purchaseItemsData);
        VBox.setVgrow(purchaseItemTableView, Priority.ALWAYS);
    }

    private void setupValidation() {
        payoutModeBox.valueProperty().addListener((options, oldValue, newValue) -> {
            boolean isOther = "Other".equals(newValue);
            otherPayoutModeField.setVisible(isOther);
            otherPayoutModeField.setManaged(isOther);
            if (!isOther) otherPayoutModeField.clear();
        });

        purchaseDatePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) { return (date != null) ? dateFormatter.format(date) : ""; }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try { return LocalDate.parse(string, dateFormatter); } catch (DateTimeParseException e) { return null; }
                }
                return null;
            }
        });
    }

    private void highlightField(Control field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void clearHighlight(Control field) {
        field.setStyle("");
    }
}