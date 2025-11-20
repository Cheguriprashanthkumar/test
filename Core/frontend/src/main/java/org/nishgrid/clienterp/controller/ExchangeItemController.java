package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.*;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExchangeItemController {

    // Main Form Fields
    @FXML private ComboBox<CustomerDTO> customerComboBox;
    @FXML private ComboBox<InvoiceSelectionDTO> invoiceComboBox;
    @FXML private ComboBox<SalesItemSelectionDTO> returnItemComboBox;
    @FXML private ComboBox<SalesItemSelectionDTO> newItemComboBox;
    @FXML private DatePicker exchangeDatePicker;
    @FXML private TextArea remarksArea;
    @FXML private TextField handledByField;
    @FXML private Button submitButton;
    @FXML private Button calculateButton;
    @FXML private Label calculationStatusLabel;

    // Rate Fields
    @FXML private TextField goldRateField;
    @FXML private TextField platinumRateField;
    @FXML private TextField silverRateField;

    // Summary Elements
    @FXML private TitledPane summaryPane;
    @FXML private Label summaryCustomerName;
    @FXML private Label summaryInvoiceNo;
    @FXML private Label summaryIntrinsicCredit;
    @FXML private Label summaryNewItemPrice;
    @FXML private Label summaryFinalAdjustment;
    @FXML private TextField calculatedAdjustmentField; // Hidden field to store calculated value

    // Exchange Log Table Elements
    @FXML private TableView<ExchangeLogRecordDTO> exchangeLogTableView;
    @FXML private TableColumn<ExchangeLogRecordDTO, String> colInvoiceNo;
    @FXML private TableColumn<ExchangeLogRecordDTO, String> colReturnItem;
    @FXML private TableColumn<ExchangeLogRecordDTO, String> colNewItem;
    @FXML private TableColumn<ExchangeLogRecordDTO, BigDecimal> colIntrinsicCredit;
    @FXML private TableColumn<ExchangeLogRecordDTO, BigDecimal> colFinalAdjustment;
    @FXML private TableColumn<ExchangeLogRecordDTO, LocalDate> colDate;
    @FXML private TableColumn<ExchangeLogRecordDTO, String> colCustomer;
    @FXML private TextField searchTextField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_BASE_URL = ApiService.getBaseUrl();
    private final String EXCHANGES_JEWELRY_API_URL = API_BASE_URL + "/exchanges/jewelry";
    private final String EXCHANGES_LOG_API_URL = API_BASE_URL + "/exchanges/log";

    private ObservableList<ExchangeLogRecordDTO> masterExchangeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureComboBoxes();
        loadInitialData();
        setupListenersForSummary();
        setupExchangeLogTable();
        loadExchangeLog();
        submitButton.setDisable(true);
        exchangeDatePicker.setValue(LocalDate.now());

        // Initial setup for mandatory rate fields
        goldRateField.setText("60.00");
        platinumRateField.setText("35.00");
        silverRateField.setText("0.70");
    }

    private void configureComboBoxes() {
        customerComboBox.setConverter(createStringConverter(CustomerDTO::getName));
        invoiceComboBox.setConverter(createStringConverter(InvoiceSelectionDTO::getInvoiceNo));
        returnItemComboBox.setConverter(createStringConverter(SalesItemSelectionDTO::getName));
        newItemComboBox.setConverter(createStringConverter(SalesItemSelectionDTO::getName));

        invoiceComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadReturnableItems(newVal.getId());
                updateSummaryInvoiceInfo(newVal);
            } else {
                returnItemComboBox.getItems().clear();
                updateSummaryInvoiceInfo(null);
            }
        });
    }

    private void setupListenersForSummary() {
        customerComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            summaryCustomerName.setText(Optional.ofNullable(newVal).map(CustomerDTO::getName).orElse(""));
            submitButton.setDisable(true);
            calculationStatusLabel.setText("Status: Ready");
        });

        // Disable submission/clear calculation whenever a key item changes
        returnItemComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> resetCalculationState());
        newItemComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> resetCalculationState());
        goldRateField.textProperty().addListener((obs, oldVal, newVal) -> resetCalculationState());
    }

    private void resetCalculationState() {
        submitButton.setDisable(true);
        calculationStatusLabel.setText("Status: Rates/Items Changed. Recalculate.");
        summaryPane.setExpanded(false);
        summaryFinalAdjustment.setText("---");
        summaryIntrinsicCredit.setText("---");
        summaryNewItemPrice.setText("---");
        calculatedAdjustmentField.clear();
    }

    private void updateSummaryInvoiceInfo(InvoiceSelectionDTO invoice) {
        summaryInvoiceNo.setText(Optional.ofNullable(invoice).map(InvoiceSelectionDTO::getInvoiceNo).orElse(""));
    }

    private void loadInitialData() {
        loadCustomers();
        loadInvoices();
        loadNewItems();
    }

    private void loadCustomers() {
        loadData(API_BASE_URL + "/customers/selection", new TypeReference<List<CustomerDTO>>() {}, customerComboBox);
    }

    private void loadInvoices() {
        loadData(API_BASE_URL + "/invoices/selection?status=Paid&status=Partially%20Returned", new TypeReference<List<InvoiceSelectionDTO>>() {}, invoiceComboBox);
    }

    private void loadNewItems() {
        loadData(API_BASE_URL + "/items/selection", new TypeReference<List<SalesItemSelectionDTO>>() {}, newItemComboBox);
    }

    private void loadReturnableItems(Long invoiceId) {
        loadData(API_BASE_URL + "/invoices/" + invoiceId + "/items", new TypeReference<List<SalesItemSelectionDTO>>() {}, returnItemComboBox);
    }

    private <T> void loadData(String url, TypeReference<List<T>> typeRef, ComboBox<T> comboBox) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> Platform.runLater(() -> {
                    try {
                        List<T> data = objectMapper.readValue(body, typeRef);
                        comboBox.setItems(FXCollections.observableArrayList(data));
                    } catch (Exception e) {
                        System.err.println("Failed to parse data from " + url);
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Data Load Error", "Failed to load data from server.");
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        System.err.println("Connection failed to " + url);
                        showAlert(Alert.AlertType.ERROR, "Connection Error", "Cannot connect to the server.");
                    });
                    return null;
                });
    }

    @FXML
    private void handleCalculateAdjustment() {
        try {
            // 1. Validation and Data Extraction
            if (getComboBoxId(customerComboBox, "Customer") == null || getComboBoxId(invoiceComboBox, "Original Invoice") == null ||
                    getComboBoxId(returnItemComboBox, "Item to Return") == null || getComboBoxId(newItemComboBox, "New Item") == null) {
                showAlert(Alert.AlertType.WARNING, "Missing Selection", "Please select Customer, Invoice, Return Item, and New Item.");
                return;
            }

            BigDecimal returnItemValue = getSelectedRetailPrice(returnItemComboBox);
            BigDecimal newItemValue = getSelectedRetailPrice(newItemComboBox);

            if (returnItemValue == null || newItemValue == null) {
                showAlert(Alert.AlertType.ERROR, "Data Error", "Cannot find price data for selected items.");
                return;
            }

            BigDecimal goldRate = parseRate(goldRateField.getText(), "Gold Rate");
            BigDecimal platinumRate = parseRate(platinumRateField.getText(), "Platinum Rate");
            BigDecimal silverRate = parseRate(silverRateField.getText(), "Silver Rate");

            // --- FAKE CALCULATION FOR FRONTEND DEMO ONLY ---
            // In a real scenario, this calculation would happen on the server.
            // Here, we simulate a basic gross difference for UI feedback.

            // Assume the returned item gets 90% of its original total price as credit
            BigDecimal intrinsicCredit = returnItemValue.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal finalAdjustment = newItemValue.subtract(intrinsicCredit).setScale(2, RoundingMode.HALF_UP);

            // 2. Update Summary UI
            summaryIntrinsicCredit.setText("₹" + intrinsicCredit.toPlainString());
            summaryNewItemPrice.setText("₹" + newItemValue.toPlainString());
            summaryFinalAdjustment.setText("₹" + finalAdjustment.toPlainString());

            // Store the calculated value for submission
            calculatedAdjustmentField.setText(finalAdjustment.toPlainString());

            // 3. Enable Submission
            summaryPane.setExpanded(true);
            submitButton.setDisable(false);
            calculationStatusLabel.setText("Status: Calculated. Final Adjustment: " + (finalAdjustment.signum() >= 0 ? "₹" + finalAdjustment : "-₹" + finalAdjustment.abs()) + ".");
            calculationStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during calculation.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmitExchange() {
        try {
            // Final validation checks
            if (submitButton.isDisable()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please calculate and verify the final adjustment before submitting.");
                return;
            }

            JewelryExchangeRequestDTO dto = new JewelryExchangeRequestDTO();
            dto.setOriginalInvoiceId(getComboBoxId(invoiceComboBox, "Original Invoice"));
            dto.setReturnItemId(getComboBoxId(returnItemComboBox, "Item to Return"));
            dto.setNewItemId(getComboBoxId(newItemComboBox, "New Item"));
            dto.setCustomerId(getComboBoxId(customerComboBox, "Customer"));
            dto.setExchangeDate(exchangeDatePicker.getValue());
            dto.setRemarks(remarksArea.getText());
            dto.setHandledBy(handledByField.getText());

            // Set mandatory rate fields
            dto.setGoldRatePerGram24K(parseRate(goldRateField.getText(), "Gold Rate"));
            dto.setPlatinumRatePerGramPure(parseRate(platinumRateField.getText(), "Platinum Rate"));
            dto.setSilverRatePerGramPure(parseRate(silverRateField.getText(), "Silver Rate"));

            // Set the final calculated amount from the hidden field
            dto.setCustomerFinalAdjustmentAmount(new BigDecimal(calculatedAdjustmentField.getText()));

            String requestBody = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EXCHANGES_JEWELRY_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> Platform.runLater(() -> {
                        if (response.statusCode() == 201) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Jewelry Exchange processed successfully!");
                            resetForm();
                            loadExchangeLog();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to process exchange (" + response.statusCode() + "): " + response.body());
                            submitButton.setDisable(true);
                            calculationStatusLabel.setText("Status: Submission Failed.");
                        }
                    }))
                    .exceptionally(e -> {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the server: " + e.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void resetForm() {
        customerComboBox.getSelectionModel().clearSelection();
        invoiceComboBox.getSelectionModel().clearSelection();
        returnItemComboBox.getItems().clear();
        newItemComboBox.getSelectionModel().clearSelection();
        remarksArea.clear();
        handledByField.clear();
        exchangeDatePicker.setValue(LocalDate.now());

        resetCalculationState(); // Also resets summary/status/submit button
        calculationStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private <T> StringConverter<T> createStringConverter(Function<T, String> nameExtractor) {
        return new StringConverter<>() {
            @Override public String toString(T object) { return object == null ? null : nameExtractor.apply(object); }
            @Override public T fromString(String string) { return null; }
        };
    }

    private Long getComboBoxId(ComboBox<?> comboBox, String fieldName) {
        Object selected = comboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return null;
        }
        if (selected instanceof CustomerDTO) return ((CustomerDTO) selected).getId();
        if (selected instanceof InvoiceSelectionDTO) return ((InvoiceSelectionDTO) selected).getId();
        if (selected instanceof SalesItemSelectionDTO) return ((SalesItemSelectionDTO) selected).getId();
        return null;
    }

    private BigDecimal getSelectedRetailPrice(ComboBox<SalesItemSelectionDTO> comboBox) {
        SalesItemSelectionDTO selected = comboBox.getSelectionModel().getSelectedItem();
        if (selected != null) {
            return selected.getUnitPrice(); // Reusing unitPrice as Retail Price for now
        }
        return null;
    }

    private BigDecimal parseRate(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " rate is required.");
        }
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " rate must be a valid number.");
        }
    }

    private void setupExchangeLogTable() {
        colInvoiceNo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInvoiceNo()));
        colReturnItem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReturnItemName()));
        colNewItem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNewItemName()));
        colIntrinsicCredit.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCalculatedIntrinsicCredit()));
        colFinalAdjustment.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCustomerFinalAdjustment()));
        colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getExchangeDate()));
        colCustomer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));

        exchangeLogTableView.setItems(masterExchangeList);
    }

    @FXML
    private void loadExchangeLog() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(EXCHANGES_LOG_API_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> Platform.runLater(() -> {
                    try {
                        List<ExchangeLogRecordDTO> exchanges = objectMapper.readValue(body, new TypeReference<List<ExchangeLogRecordDTO>>() {});
                        masterExchangeList.clear();
                        masterExchangeList.addAll(exchanges);
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load exchange log.");
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Cannot connect to server."));
                    return null;
                });
    }

    @FXML
    private void handleSearch() {
        String searchText = searchTextField.getText().toLowerCase();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<ExchangeLogRecordDTO> filteredList = masterExchangeList.stream()
                .filter(record -> {
                    boolean textMatch = searchText.isEmpty() ||
                            record.getInvoiceNo().toLowerCase().contains(searchText) ||
                            record.getReturnItemName().toLowerCase().contains(searchText) ||
                            record.getNewItemName().toLowerCase().contains(searchText) ||
                            record.getCustomerName().toLowerCase().contains(searchText);

                    boolean dateMatch = true;
                    if (startDate != null && record.getExchangeDate().isBefore(startDate)) {
                        dateMatch = false;
                    }
                    if (endDate != null && record.getExchangeDate().isAfter(endDate)) {
                        dateMatch = false;
                    }

                    return textMatch && dateMatch;
                })
                .collect(Collectors.toList());

        exchangeLogTableView.setItems(FXCollections.observableArrayList(filteredList));

        if (filteredList.isEmpty() && !searchText.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No matching records found.");
        }
    }
}