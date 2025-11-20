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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nishgrid.clienterp.dto.SaleRequestDTO;
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
import java.util.regex.Pattern;

public class RegistrationController {

    @FXML private TextField customerNameField;
    @FXML private TextField customerMobileField;
    @FXML private TextArea customerAddressField;
    @FXML private TextField invoiceNoField;
    @FXML private DatePicker invoiceDatePicker;
    @FXML private ComboBox<String> paymentModeBox;
    @FXML private TextField otherPaymentModeField;
    @FXML private TextField totalAmountField;
    @FXML private TextField netAmountField;
    @FXML private TextArea remarksField;
    @FXML private TextField itemNameField;
    @FXML private TextField purityField;
    @FXML private TextField grossWeightField;
    @FXML private TextField netWeightField;
    @FXML private TextField ratePerGramField;
    @FXML private TextField makingChargeField;
    @FXML private TextField totalPriceField;
    @FXML private TableView<SaleRequestDTO.SaleItemDTO> salesItemTableView;
    @FXML private TableColumn<SaleRequestDTO.SaleItemDTO, String> itemNameColumn;
    @FXML private TableColumn<SaleRequestDTO.SaleItemDTO, String> purityColumn;
    @FXML private TableColumn<SaleRequestDTO.SaleItemDTO, Double> grossWeightColumn;
    @FXML private TableColumn<SaleRequestDTO.SaleItemDTO, Double> netWeightColumn;
    @FXML private TableColumn<SaleRequestDTO.SaleItemDTO, BigDecimal> ratePerGramColumn;
    @FXML private TableColumn<SaleRequestDTO.SaleItemDTO, BigDecimal> makingChargeColumn;
    @FXML private TextField discountAmountField;
    @FXML private TextField discountPercentField;
    @FXML private TextField diamondCaratField;
    @FXML private TextField diamondRateField;
    @FXML private TextField makingChargeAmountField;
    @FXML private TableColumn<SaleRequestDTO.SaleItemDTO, BigDecimal> diamondCaratColumn;
    @FXML private Button addItemButton;
    @FXML private TextField oldGoldValueField;

    private final ObservableList<SaleRequestDTO.SaleItemDTO> salesItemsData = FXCollections.observableArrayList();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final HttpClient client = HttpClient.newHttpClient();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private SaleRequestDTO.SaleItemDTO selectedItem = null;
    private boolean isUpdatingDiscountFields = false;

    @Data
    @NoArgsConstructor
    private static class ErrorResponseDTO { private String message; }

    @FXML
    private void initialize() {
        setupSalesItemTable();
        addCalculationListeners();
        addPaymentModeListener();
        setupValidation();
        setupFieldNavigation();
        fetchAndSetNextInvoiceNumber();
        invoiceDatePicker.setValue(LocalDate.now());

        salesItemTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                clearSalesItemInputs();
            }
        });
    }

    private void addCalculationListeners() {
        netWeightField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalPrice());
        ratePerGramField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalPrice());
        makingChargeField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalPrice());
        makingChargeAmountField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalPrice());
        diamondCaratField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalPrice());
        diamondRateField.textProperty().addListener((obs, oldV, newV) -> updateItemTotalPrice());
        salesItemsData.addListener((ListChangeListener<SaleRequestDTO.SaleItemDTO>) c -> updateInvoiceTotals());
        oldGoldValueField.textProperty().addListener((obs, oldV, newV) -> updateInvoiceTotals());

        discountAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdatingDiscountFields) return;
            isUpdatingDiscountFields = true;
            BigDecimal totalWithGst = calculateTotalWithGst();
            BigDecimal discountAmount = parseBigDecimalSafe(newVal);
            if (totalWithGst.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percent = discountAmount.multiply(new BigDecimal("100")).divide(totalWithGst, 2, RoundingMode.HALF_UP);
                discountPercentField.setText(percent.toPlainString());
            } else {
                discountPercentField.setText("0.00");
            }
            isUpdatingDiscountFields = false;
            updateInvoiceTotals();
        });

        discountPercentField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdatingDiscountFields) return;
            isUpdatingDiscountFields = true;
            BigDecimal totalWithGst = calculateTotalWithGst();
            BigDecimal discountPercent = parseBigDecimalSafe(newVal);
            BigDecimal amount = totalWithGst.multiply(discountPercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            discountAmountField.setText(amount.toPlainString());
            isUpdatingDiscountFields = false;
            updateInvoiceTotals();
        });
    }

    private BigDecimal calculateTotalItemAmount() {
        return salesItemsData.stream()
                .map(item -> {
                    BigDecimal netWeight = Optional.of(item.getNetWeight()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
                    BigDecimal rate = Optional.ofNullable(item.getRatePerGram()).orElse(BigDecimal.ZERO);
                    BigDecimal goldValue = netWeight.multiply(rate);
                    BigDecimal diamondCarat = Optional.ofNullable(item.getDiamondCarat()).orElse(BigDecimal.ZERO);
                    BigDecimal diamondRate = Optional.ofNullable(item.getDiamondRate()).orElse(BigDecimal.ZERO);
                    BigDecimal diamondValue = diamondCarat.multiply(diamondRate);
                    BigDecimal baseValue = goldValue.add(diamondValue);
                    BigDecimal makingChargeValue;
                    BigDecimal fixedMakingCharge = Optional.ofNullable(item.getMakingChargeAmount()).orElse(BigDecimal.ZERO);
                    if (fixedMakingCharge.compareTo(BigDecimal.ZERO) > 0) {
                        makingChargeValue = fixedMakingCharge;
                    } else {
                        BigDecimal makingPercent = Optional.ofNullable(item.getMakingCharge()).orElse(BigDecimal.ZERO);
                        makingChargeValue = baseValue.multiply(makingPercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    }
                    return baseValue.add(makingChargeValue);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalWithGst() {
        BigDecimal totalAmount = calculateTotalItemAmount();
        BigDecimal gstPercent = BigDecimal.ZERO; // GST is removed
        BigDecimal gstAmount = totalAmount.multiply(gstPercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return totalAmount.add(gstAmount);
    }

    private void updateInvoiceTotals() {
        BigDecimal totalAmount = calculateTotalItemAmount();
        totalAmountField.setText(String.format("%.2f", totalAmount));
        BigDecimal totalAfterDiscount = totalAmount.subtract(parseBigDecimalSafe(discountAmountField.getText()));
        BigDecimal netAmount = totalAfterDiscount.subtract(parseBigDecimalSafe(oldGoldValueField.getText()));
        netAmountField.setText(String.format("%.2f", netAmount));
    }

    private void updateItemTotalPrice() {
        BigDecimal netWeight = parseBigDecimalSafe(netWeightField.getText());
        BigDecimal rate = parseBigDecimalSafe(ratePerGramField.getText());
        BigDecimal goldValue = netWeight.multiply(rate);
        BigDecimal diamondCarat = parseBigDecimalSafe(diamondCaratField.getText());
        BigDecimal diamondRate = parseBigDecimalSafe(diamondRateField.getText());
        BigDecimal diamondValue = diamondCarat.multiply(diamondRate);
        BigDecimal baseValue = goldValue.add(diamondValue);
        BigDecimal makingChargeValue;
        BigDecimal fixedMakingCharge = parseBigDecimalSafe(makingChargeAmountField.getText());
        if (fixedMakingCharge.compareTo(BigDecimal.ZERO) > 0) {
            makingChargeValue = fixedMakingCharge;
            if (!makingChargeField.isFocused()) {
                makingChargeField.clear();
            }
        } else {
            BigDecimal makingPercent = parseBigDecimalSafe(makingChargeField.getText());
            makingChargeValue = baseValue.multiply(makingPercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        BigDecimal total = baseValue.add(makingChargeValue);
        totalPriceField.setText(String.format("%.2f", total));
    }

    @FXML
    private void handleAddItem() {
        clearFieldHighlights();
        if (!validateItemInputs()) {
            return;
        }
        SaleRequestDTO.SaleItemDTO item = (selectedItem != null) ? selectedItem : new SaleRequestDTO.SaleItemDTO();
        item.setItemName(itemNameField.getText());
        item.setPurity(purityField.getText());
        item.setGrossWeight(parseDoubleSafe(grossWeightField.getText()));
        item.setNetWeight(parseDoubleSafe(netWeightField.getText()));
        item.setRatePerGram(parseBigDecimalSafe(ratePerGramField.getText()));
        item.setMakingCharge(parseBigDecimalSafe(makingChargeField.getText()));
        item.setMakingChargeAmount(parseBigDecimalSafe(makingChargeAmountField.getText()));
        item.setDiamondCarat(parseBigDecimalSafe(diamondCaratField.getText()));
        item.setDiamondRate(parseBigDecimalSafe(diamondRateField.getText()));
        if (selectedItem == null) {
            salesItemsData.add(item);
        } else {
            salesItemTableView.refresh();
        }
        clearSalesItemInputs();
        itemNameField.requestFocus();
    }

    @FXML
    private void handleEditSelectedItem() {
        SaleRequestDTO.SaleItemDTO selected = salesItemTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to edit.");
            return;
        }
        selectedItem = selected;
        populateItemFields(selected);
    }

    @FXML
    private void handleDeleteItem() {
        SaleRequestDTO.SaleItemDTO selected = salesItemTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            salesItemsData.remove(selected);
            clearSalesItemInputs();
        } else {
            showAlert(AlertType.WARNING, "No Selection", "Please select an item to delete.");
        }
    }

    @FXML
    private void handleSubmit() {
        if (!validateOverallForm()) return;
        try {
            SaleRequestDTO dto = new SaleRequestDTO();
            dto.setCustomerName(customerNameField.getText());
            dto.setCustomerMobile(customerMobileField.getText());
            dto.setCustomerAddress(customerAddressField.getText());
            dto.setInvoiceDate(invoiceDatePicker.getValue());
            dto.setDiscountAmount(parseBigDecimalSafe(discountAmountField.getText()));
            dto.setDiscountPercent(parseBigDecimalSafe(discountPercentField.getText()));
            dto.setRemarks(remarksField.getText());
            dto.setPaymentMode(paymentModeBox.getValue());
            if ("Other".equals(paymentModeBox.getValue())) {
                dto.setOtherPaymentMode(otherPaymentModeField.getText());
            }
            dto.setItems(new ArrayList<>(salesItemsData));
            dto.setOldGoldValue(parseBigDecimalSafe(oldGoldValueField.getText()));
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiService.getBaseUrl() + "/sales"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> Platform.runLater(() -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            showAlert(AlertType.INFORMATION, "Success", "Sale saved successfully!");
                            resetForm();
                        } else {
                            showErrorAlert("Error: " + response.body());
                        }
                    }))
                    .exceptionally(e -> {
                        Platform.runLater(() -> showErrorAlert("Connection error: " + e.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Unexpected error: " + e.getMessage());
        }
    }

    private boolean validateItemInputs() {
        List<String> errors = new ArrayList<>();
        if (itemNameField.getText().isBlank()) {
            errors.add("• Item Name is required.");
            highlightField(itemNameField);
        }
        BigDecimal totalPrice = parseBigDecimalSafe(totalPriceField.getText());
        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("• Total item price must be greater than 0.");
            highlightField(netWeightField);
            highlightField(ratePerGramField);
        }
        if (!errors.isEmpty()) {
            showErrorAlert("Please correct the following item details:\n\n" + String.join("\n", errors));
            return false;
        }
        return true;
    }

    private boolean validateOverallForm() {
        clearFieldHighlights();
        if (customerNameField.getText().isBlank()) {
            showErrorAlert("Customer name is required.");
            highlightField(customerNameField);
            return false;
        }
        if (customerMobileField.getText().length() != 10) {
            showErrorAlert("Mobile number must be 10 digits.");
            highlightField(customerMobileField);
            return false;
        }
        if (invoiceDatePicker.getValue() == null) {
            showErrorAlert("Invoice date is required.");
            highlightField(invoiceDatePicker.getEditor());
            return false;
        }
        if (paymentModeBox.getValue() == null || paymentModeBox.getValue().isBlank()) {
            showErrorAlert("Select a payment mode.");
            highlightField(paymentModeBox);
            return false;
        }
        if ("Other".equals(paymentModeBox.getValue()) && otherPaymentModeField.getText().isBlank()) {
            showErrorAlert("Specify other payment mode.");
            highlightField(otherPaymentModeField);
            return false;
        }
        if (salesItemsData.isEmpty()) {
            showErrorAlert("Add at least one item to the sale.");
            return false;
        }
        return true;
    }

    private void resetForm() {
        customerNameField.clear();
        customerMobileField.clear();
        customerAddressField.clear();
        invoiceDatePicker.setValue(LocalDate.now());
        paymentModeBox.getSelectionModel().clearSelection();
        otherPaymentModeField.clear();
        discountAmountField.setText("0");
        discountPercentField.setText("0");
        remarksField.clear();
        oldGoldValueField.clear();
        salesItemsData.clear();
        clearSalesItemInputs();
        clearFieldHighlights();
        fetchAndSetNextInvoiceNumber();
        customerNameField.requestFocus();
    }

    private void populateItemFields(SaleRequestDTO.SaleItemDTO item) {
        itemNameField.setText(item.getItemName());
        purityField.setText(item.getPurity());
        grossWeightField.setText(String.valueOf(item.getGrossWeight()));
        netWeightField.setText(String.valueOf(item.getNetWeight()));
        ratePerGramField.setText(String.valueOf(item.getRatePerGram() != null ? item.getRatePerGram() : ""));
        makingChargeField.setText(String.valueOf(item.getMakingCharge() != null ? item.getMakingCharge() : ""));
        makingChargeAmountField.setText(String.valueOf(item.getMakingChargeAmount() != null ? item.getMakingChargeAmount() : ""));
        diamondCaratField.setText(String.valueOf(item.getDiamondCarat() != null ? item.getDiamondCarat() : ""));
        diamondRateField.setText(String.valueOf(item.getDiamondRate() != null ? item.getDiamondRate() : ""));
        updateItemTotalPrice();
    }

    private void clearSalesItemInputs() {
        itemNameField.clear();
        purityField.clear();
        grossWeightField.clear();
        netWeightField.clear();
        ratePerGramField.clear();
        makingChargeField.clear();
        totalPriceField.clear();
        makingChargeAmountField.clear();
        diamondCaratField.clear();
        diamondRateField.clear();
        salesItemTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }

    private void fetchAndSetNextInvoiceNumber() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ApiService.getBaseUrl() + "/sales/next-invoice-number")).header("Content-Type", "application/json").GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() == 200) {
                try {
                    Map<String, String> responseMap = mapper.readValue(response.body(), Map.class);
                    String nextInvoiceNumber = responseMap.get("nextInvoiceNumber");
                    Platform.runLater(() -> invoiceNoField.setText(nextInvoiceNumber.replace("INV", "RE")));
                } catch (Exception e) {
                    Platform.runLater(() -> invoiceNoField.setPromptText("Error fetching invoice"));
                }
            }
        }).exceptionally(e -> {
            Platform.runLater(() -> invoiceNoField.setPromptText("Connection Error"));
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
        if (message.lines().count() > 3) {
            TextArea textArea = new TextArea(message);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            alert.getDialogPane().setContent(textArea);
        }
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
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

    private void setupSalesItemTable() {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        purityColumn.setCellValueFactory(new PropertyValueFactory<>("purity"));
        grossWeightColumn.setCellValueFactory(new PropertyValueFactory<>("grossWeight"));
        netWeightColumn.setCellValueFactory(new PropertyValueFactory<>("netWeight"));
        ratePerGramColumn.setCellValueFactory(new PropertyValueFactory<>("ratePerGram"));
        makingChargeColumn.setCellValueFactory(new PropertyValueFactory<>("makingCharge"));
        diamondCaratColumn.setCellValueFactory(new PropertyValueFactory<>("diamondCarat"));
        salesItemTableView.setItems(salesItemsData);
        VBox.setVgrow(salesItemTableView, Priority.ALWAYS);
    }

    private void setupValidation() {
        UnaryOperator<TextFormatter.Change> tenDigitFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d{0,10}") ? change : null;
        };
        customerMobileField.setTextFormatter(new TextFormatter<>(tenDigitFilter));
        customerNameField.textProperty().addListener((obs, oldV, newV) -> { if (!newV.isBlank()) clearHighlight(customerNameField); });
        customerMobileField.textProperty().addListener((obs, oldV, newV) -> { if (newV != null && newV.length() == 10) clearHighlight(customerMobileField); });
        invoiceDatePicker.valueProperty().addListener((obs, oldV, newV) -> { if (newV != null) clearHighlight(invoiceDatePicker.getEditor()); });
        paymentModeBox.valueProperty().addListener((obs, oldV, newV) -> { if (newV != null && !newV.isBlank()) clearHighlight(paymentModeBox); });
        otherPaymentModeField.textProperty().addListener((obs, oldV, newV) -> { if (!newV.isBlank()) clearHighlight(otherPaymentModeField); });
        itemNameField.textProperty().addListener((obs, oldV, newV) -> { if (!newV.isBlank()) clearHighlight(itemNameField); });
        netWeightField.textProperty().addListener((obs, oldV, newV) -> clearHighlight(netWeightField));
        ratePerGramField.textProperty().addListener((obs, oldV, newV) -> clearHighlight(ratePerGramField));
        invoiceDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
        invoiceDatePicker.setConverter(new StringConverter<>() {
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

    private void clearFieldHighlights() {
        Control[] controls = {customerNameField, customerMobileField, invoiceDatePicker.getEditor(), paymentModeBox, otherPaymentModeField, itemNameField, totalPriceField, netWeightField, ratePerGramField};
        for (Control c : controls) {
            c.setStyle("");
        }
    }

    private void addPaymentModeListener() {
        paymentModeBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            boolean isOther = "Other".equals(newValue);
            otherPaymentModeField.setVisible(isOther);
            otherPaymentModeField.setManaged(isOther);
            if (!isOther) otherPaymentModeField.clear();
        });
    }

    private void highlightField(Control field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void clearHighlight(Control field) {
        field.setStyle("");
    }

    private void setupFieldNavigation() {
        customerNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                if (customerNameField.getText().isBlank()) {
                    showWarningAlert("Input Required", "Please enter a customer name to proceed.");
                    highlightField(customerNameField);
                } else {
                    customerMobileField.requestFocus();
                }
            }
        });

        customerMobileField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                if (customerMobileField.getText().length() != 10) {
                    showWarningAlert("Input Invalid", "Mobile number must be exactly 10 digits.");
                    highlightField(customerMobileField);
                } else {
                    customerAddressField.requestFocus();
                }
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                customerNameField.requestFocus();
            }
        });

        customerAddressField.setOnKeyPressed(event -> {
            if (event.isControlDown() && (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN)) {
                event.consume();
                invoiceDatePicker.requestFocus();
            } else if (event.isControlDown() && event.getCode() == KeyCode.UP) {
                event.consume();
                customerMobileField.requestFocus();
            }
        });

        invoiceDatePicker.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                if(invoiceDatePicker.getValue() == null) {
                    showWarningAlert("Input Required", "Please select an invoice date.");
                    highlightField(invoiceDatePicker);
                } else {
                    paymentModeBox.requestFocus();
                }
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                customerAddressField.requestFocus();
            }
        });

        paymentModeBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if ("Other".equals(paymentModeBox.getValue())) {
                    otherPaymentModeField.requestFocus();
                } else {
                    remarksField.requestFocus();
                }
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                invoiceDatePicker.requestFocus();
            }
        });

        otherPaymentModeField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                if (otherPaymentModeField.getText().isBlank()) {
                    showWarningAlert("Input Required", "Please specify the payment mode.");
                    highlightField(otherPaymentModeField);
                } else {
                    remarksField.requestFocus();
                }
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                paymentModeBox.requestFocus();
            }
        });

        remarksField.setOnKeyPressed(event -> {
            if (event.isControlDown() && (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN)) {
                event.consume();
                discountPercentField.requestFocus();
            } else if (event.isControlDown() && event.getCode() == KeyCode.UP) {
                event.consume();
                if (otherPaymentModeField.isVisible()) {
                    otherPaymentModeField.requestFocus();
                } else {
                    paymentModeBox.requestFocus();
                }
            }
        });

        discountAmountField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, remarksField, discountPercentField));
        discountPercentField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, discountAmountField, oldGoldValueField));
        oldGoldValueField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, discountPercentField, itemNameField));

        itemNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                if (itemNameField.getText().isBlank()) {
                    showWarningAlert("Input Required", "Please enter an item name to proceed.");
                    highlightField(itemNameField);
                } else {
                    purityField.requestFocus();
                }
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                oldGoldValueField.requestFocus();
            }
        });

        purityField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, itemNameField, grossWeightField));
        grossWeightField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, purityField, netWeightField));
        netWeightField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, grossWeightField, ratePerGramField));
        ratePerGramField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, netWeightField, makingChargeField));
        makingChargeField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, ratePerGramField, makingChargeAmountField));
        makingChargeAmountField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, makingChargeField, diamondCaratField));
        diamondCaratField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, makingChargeAmountField, diamondRateField));
        diamondRateField.setOnKeyPressed(e -> handleSimpleKeyEvent(e, diamondCaratField, addItemButton));

        addItemButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                diamondRateField.requestFocus();
                e.consume();
            } else if (e.getCode() == KeyCode.DOWN) {
                customerNameField.requestFocus();
                e.consume();
            }
        });
    }

    private void handleSimpleKeyEvent(KeyEvent event, Control up, Control down) {
        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
            if (down != null) down.requestFocus();
            event.consume();
        } else if (event.getCode() == KeyCode.UP) {
            if (up != null) up.requestFocus();
            event.consume();
        }
    }
}