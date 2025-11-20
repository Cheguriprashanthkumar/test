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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.InvoiceSelectionDTO;
import org.nishgrid.clienterp.dto.ReturnRecordDTO;
import org.nishgrid.clienterp.dto.ReturnRequestDTO;
import org.nishgrid.clienterp.dto.SalesItemSelectionDTO;
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
import java.util.stream.Collectors;

public class ReturnSaleController {

    @FXML private ComboBox<InvoiceSelectionDTO> invoiceComboBox;
    @FXML private DatePicker returnDatePicker;
    @FXML private TextArea returnReasonArea;
    @FXML private TextField totalReturnAmountField;
    @FXML private ComboBox<String> refundModeBox;
    @FXML private TextField handledByField;
    @FXML private Button submitButton;
    @FXML private TextField searchTextField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<ReturnRecordDTO> returnLogTableView;
    @FXML private TableColumn<ReturnRecordDTO, String> colInvoice;
    @FXML private TableColumn<ReturnRecordDTO, String> colItem;
    @FXML private TableColumn<ReturnRecordDTO, LocalDate> colReturnDate;
    @FXML private TableColumn<ReturnRecordDTO, BigDecimal> colAmount;
    @FXML private TableColumn<ReturnRecordDTO, Integer> colQuantity;
    @FXML private TableColumn<ReturnRecordDTO, String> colReason;
    @FXML private TableColumn<ReturnRecordDTO, String> colHandledBy;
    @FXML private TableView<SalesItemSelectionDTO> availableItemsTable;
    @FXML private TableColumn<SalesItemSelectionDTO, String> colAvailableItem;
    @FXML private TableColumn<SalesItemSelectionDTO, Integer> colAvailableQty;
    @FXML private TableColumn<SalesItemSelectionDTO, BigDecimal> colAvailablePrice;
    @FXML private TableView<ReturnItemWrapper> selectedItemsTable;
    @FXML private TableColumn<ReturnItemWrapper, String> colSelectedItem;
    @FXML private TableColumn<ReturnItemWrapper, Integer> colReturnQty;
    @FXML private TableColumn<ReturnItemWrapper, BigDecimal> colReturnAmount;
    @FXML private TableColumn<ReturnItemWrapper, Void> colRemove;
    @FXML private Button addToReturnListButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_BASE_URL = ApiService.getBaseUrl();
    private final String INVOICES_API_URL = API_BASE_URL + "/invoices/selection?status=Paid&status=Partially%20Returned";
    private final String RETURNS_API_URL = API_BASE_URL + "/returns/batch";

    private final ObservableList<ReturnItemWrapper> returnList = FXCollections.observableArrayList();
    private ObservableList<SalesItemSelectionDTO> availableItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureFormComponents();
        setupTableView();
        loadInvoices();
        loadReturnLog();
        setupFieldNavigation();
    }

    private void configureFormComponents() {
        returnDatePicker.setValue(LocalDate.now());

        invoiceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(InvoiceSelectionDTO invoice) { return invoice == null ? null : invoice.getInvoiceNo(); }
            @Override public InvoiceSelectionDTO fromString(String string) { return null; }
        });

        invoiceComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getId() != null) {
                loadItemsForInvoice(newVal.getId());
                returnList.clear();
                updateTotalReturnAmount();
            } else {
                availableItems.clear();
                returnList.clear();
                updateTotalReturnAmount();
            }
        });
    }

    private void setupTableView() {
        colInvoice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInvoiceNo()));
        colItem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemName()));
        colReturnDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReturnDate()));
        colAmount.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReturnAmount()));
        colQuantity.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        colReason.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReturnReason()));
        colHandledBy.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHandledBy()));

        colAvailableItem.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAvailableQty.setCellValueFactory(new PropertyValueFactory<>("quantityRemaining"));
        colAvailablePrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        availableItemsTable.setItems(availableItems);

        colSelectedItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colReturnQty.setCellValueFactory(cellData -> cellData.getValue().returnQuantityProperty());
        colReturnAmount.setCellValueFactory(cellData -> cellData.getValue().returnAmountProperty());

        colReturnQty.setCellFactory(tc -> new TableCell<ReturnItemWrapper, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>();
            private final Tooltip tooltip = new Tooltip();

            {
                spinner.valueFactoryProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        spinner.valueFactoryProperty().set(newVal);
                    }
                });

                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        ReturnItemWrapper item = getTableRow().getItem();
                        item.setReturnQuantity(newVal);
                        updateTotalReturnAmount();
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    ReturnItemWrapper wrapper = (ReturnItemWrapper) getTableRow().getItem();
                    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, wrapper.getOriginalQuantity(), wrapper.getReturnQuantity());
                    spinner.setValueFactory(valueFactory);
                    tooltip.setText("Max returnable: " + wrapper.getOriginalQuantity());
                    spinner.setTooltip(tooltip);
                    setGraphic(spinner);
                }
            }
        });

        colRemove.setCellFactory(tc -> new TableCell<ReturnItemWrapper, Void>() {
            private final Button btn = new Button("Remove");
            {
                btn.setOnAction(event -> {
                    ReturnItemWrapper item = getTableRow().getItem();
                    if (item != null) {
                        SalesItemSelectionDTO originalItem = new SalesItemSelectionDTO();
                        originalItem.setId(item.getSalesItemId());
                        originalItem.setName(item.getItemName());
                        originalItem.setQuantityRemaining(item.getOriginalQuantity());
                        originalItem.setUnitPrice(item.getUnitPrice());
                        availableItems.add(originalItem);
                        returnList.remove(item);
                        updateTotalReturnAmount();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        selectedItemsTable.setItems(returnList);
    }

    private void loadInvoices() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(INVOICES_API_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    Platform.runLater(() -> {
                        try {
                            if (body == null || body.isEmpty()) {
                                showAlert(Alert.AlertType.INFORMATION, "No Invoices", "No paid invoices found.");
                                return;
                            }
                            List<InvoiceSelectionDTO> invoices = objectMapper.readValue(body, new TypeReference<>() {});
                            if (invoices != null && !invoices.isEmpty()) {
                                invoiceComboBox.setItems(FXCollections.observableArrayList(invoices));
                            }
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load invoices.");
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Cannot connect to server: " + e.getMessage()));
                    return null;
                });
    }

    private void loadItemsForInvoice(Long invoiceId) {
        String url = API_BASE_URL + "/invoices/" + invoiceId + "/items/not-returned";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    Platform.runLater(() -> {
                        try {
                            if (body == null || body.isEmpty()) {
                                availableItems.clear();
                                showAlert(Alert.AlertType.INFORMATION, "No Items", "No returnable items for this invoice.");
                                return;
                            }
                            ApiResponse<List<SalesItemSelectionDTO>> response = objectMapper.readValue(body, new TypeReference<>() {});
                            List<SalesItemSelectionDTO> items = response.getData();
                            if (items == null || items.isEmpty()) {
                                availableItems.clear();
                                showAlert(Alert.AlertType.INFORMATION, "No Items", "No returnable items for this invoice.");
                            } else {
                                availableItems.setAll(items);
                            }
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load items for invoice.");
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Cannot connect to server: " + e.getMessage()));
                    return null;
                });
    }

    @FXML
    private void handleAddToReturnList() {
        SalesItemSelectionDTO selectedItem = availableItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No Item Selected", "Please select an item from the list of available items.");
            return;
        }

        Optional<ReturnItemWrapper> existingItem = returnList.stream()
                .filter(item -> item.getSalesItemId().equals(selectedItem.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Item", "This item has already been added to the return list. Please select another item.");
            return;
        }

        ReturnItemWrapper wrapper = new ReturnItemWrapper(selectedItem);
        returnList.add(wrapper);
        availableItems.remove(selectedItem);
        updateTotalReturnAmount();
    }

    private void updateTotalReturnAmount() {
        BigDecimal total = returnList.stream()
                .map(ReturnItemWrapper::getReturnAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalReturnAmountField.setText(total.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    @FXML
    private void handleSubmitReturn() {
        if (returnList.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No Items Selected", "Please add at least one item to the return list.");
            return;
        }
        if (invoiceComboBox.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select an invoice.");
            return;
        }
        try {
            List<ReturnRequestDTO> returnRequests = returnList.stream()
                    .map(wrapper -> {
                        ReturnRequestDTO dto = new ReturnRequestDTO();
                        dto.setInvoiceId(invoiceComboBox.getSelectionModel().getSelectedItem().getId());
                        dto.setSalesItemId(wrapper.getSalesItemId());
                        dto.setReturnDate(returnDatePicker.getValue());
                        dto.setReturnReason(returnReasonArea.getText());
                        dto.setQuantity(wrapper.getReturnQuantity());
                        dto.setReturnAmount(wrapper.getReturnAmount());
                        dto.setRefundMode(refundModeBox.getValue());
                        dto.setHandledBy(handledByField.getText());
                        return dto;
                    })
                    .collect(Collectors.toList());

            String requestBody = objectMapper.writeValueAsString(returnRequests);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/returns/batch"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> Platform.runLater(() -> {
                        if (response.statusCode() == 201) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "All selected items returned successfully!");
                            resetForm();
                            loadReturnLog();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to process returns. Server: " + response.statusCode() + "\n" + response.body());
                        }
                    })).exceptionally(e -> {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the server: " + e.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetForm() {
        invoiceComboBox.getSelectionModel().clearSelection();
        availableItems.clear();
        returnList.clear();
        returnReasonArea.clear();
        totalReturnAmountField.clear();
        handledByField.clear();
        returnDatePicker.setValue(LocalDate.now());
        refundModeBox.setValue("Cash");
    }

    private void setupFieldNavigation() {
        invoiceComboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                availableItemsTable.requestFocus();
            }
        });
        availableItemsTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                handleAddToReturnList();
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                if (availableItemsTable.getSelectionModel().getSelectedIndex() == 0) {
                    invoiceComboBox.requestFocus();
                }
            }
        });
        addToReturnListButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                selectedItemsTable.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                availableItemsTable.requestFocus();
            }
        });
        selectedItemsTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                returnDatePicker.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                if (selectedItemsTable.getSelectionModel().getSelectedIndex() == 0) {
                    addToReturnListButton.requestFocus();
                }
            }
        });
        returnDatePicker.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                returnReasonArea.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                selectedItemsTable.requestFocus();
            }
        });
        returnReasonArea.setOnKeyPressed(event -> {
            if (event.isControlDown() && (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN)) {
                event.consume();
                refundModeBox.requestFocus();
            } else if (event.isControlDown() && event.getCode() == KeyCode.UP) {
                event.consume();
                returnDatePicker.requestFocus();
            }
        });
        refundModeBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                handledByField.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                returnReasonArea.requestFocus();
            }
        });
        handledByField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
                event.consume();
                submitButton.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                event.consume();
                refundModeBox.requestFocus();
            }
        });
        submitButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                event.consume();
                handledByField.requestFocus();
            }
        });
    }

    private void loadReturnLog() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/returns")).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> Platform.runLater(() -> {
                    try {
                        if (body == null || body.trim().isEmpty()) {
                            returnLogTableView.setItems(FXCollections.observableArrayList());
                            return;
                        }
                        List<ReturnRecordDTO> returns = objectMapper.readValue(body, new TypeReference<>() {});
                        returnLogTableView.setItems(FXCollections.observableArrayList(returns));
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load return log: " + e.getMessage());
                        e.printStackTrace();
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to server: " + e.getMessage()));
                    return null;
                });
    }

    @FXML
    private void handleSearch() {
        String searchText = searchTextField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<ReturnRecordDTO> filteredList = returnLogTableView.getItems().stream()
                .filter(record -> {
                    boolean textMatch = searchText == null || searchText.trim().isEmpty() ||
                            record.getInvoiceNo().toLowerCase().contains(searchText.toLowerCase()) ||
                            record.getItemName().toLowerCase().contains(searchText.toLowerCase()) ||
                            record.getReturnReason().toLowerCase().contains(searchText.toLowerCase()) ||
                            record.getHandledBy().toLowerCase().contains(searchText.toLowerCase());

                    boolean dateMatch = true;
                    if (startDate != null && record.getReturnDate().isBefore(startDate)) {
                        dateMatch = false;
                    }
                    if (endDate != null && record.getReturnDate().isAfter(endDate)) {
                        dateMatch = false;
                    }

                    return textMatch && dateMatch;
                })
                .collect(Collectors.toList());

        returnLogTableView.setItems(FXCollections.observableArrayList(filteredList));

        if (filteredList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No matching records found for your search criteria.");
        }
    }

    public static class ReturnItemWrapper {
        private final Long salesItemId;
        private final String itemName;
        private final BigDecimal unitPrice;
        private final int originalQuantity;
        private final SimpleObjectProperty<Integer> returnQuantity;
        private final SimpleObjectProperty<BigDecimal> returnAmount;

        public ReturnItemWrapper(SalesItemSelectionDTO item) {
            this.salesItemId = item.getId();
            this.itemName = item.getName();
            this.unitPrice = item.getUnitPrice();
            this.originalQuantity = item.getQuantityRemaining();
            this.returnQuantity = new SimpleObjectProperty<>(1);
            this.returnAmount = new SimpleObjectProperty<>(unitPrice.multiply(BigDecimal.valueOf(1)).setScale(2, RoundingMode.HALF_UP));

            this.returnQuantity.addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    this.returnAmount.set(unitPrice.multiply(BigDecimal.valueOf(newVal)).setScale(2, RoundingMode.HALF_UP));
                }
            });
        }

        public Long getSalesItemId() { return salesItemId; }
        public String getItemName() { return itemName; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public int getOriginalQuantity() { return originalQuantity; }

        public SimpleObjectProperty<Integer> returnQuantityProperty() { return returnQuantity; }
        public void setReturnQuantity(Integer qty) { this.returnQuantity.set(qty); }
        public Integer getReturnQuantity() { return this.returnQuantity.get(); }

        public SimpleObjectProperty<BigDecimal> returnAmountProperty() { return returnAmount; }
        public BigDecimal getReturnAmount() { return this.returnAmount.get(); }
    }

    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}