package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.PurchaseOrderRequest;
import org.nishgrid.clienterp.model.ProductCatalog;
import org.nishgrid.clienterp.model.PurchaseOrder;
import org.nishgrid.clienterp.model.PurchaseOrderItem;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class PurchaseOrderController {

    private final ApiService apiService = new ApiService();
    private final ObservableList<PurchaseOrderRequest.PurchaseOrderItemDTO> poItems = FXCollections.observableArrayList();
    private PurchaseOrder selectedOrder = null;

    @FXML private TextField poNumberField;
    @FXML private DatePicker orderDatePicker;
    @FXML private ComboBox<Vendor> vendorComboBox;
    @FXML private TextField remarksField;

    @FXML private ComboBox<ProductCatalog> productComboBox;
    @FXML private TextField purityField;
    @FXML private TextField weightField;
    @FXML private TextField rateField;
    @FXML private TextField taxField;
    @FXML private TextField totalPriceField;

    @FXML private TableView<PurchaseOrderRequest.PurchaseOrderItemDTO> poItemsTable;
    @FXML private TableColumn<PurchaseOrderRequest.PurchaseOrderItemDTO, String> colProductName;
    @FXML private TableColumn<PurchaseOrderRequest.PurchaseOrderItemDTO, String> colPurity;
    @FXML private TableColumn<PurchaseOrderRequest.PurchaseOrderItemDTO, BigDecimal> colWeight;
    @FXML private TableColumn<PurchaseOrderRequest.PurchaseOrderItemDTO, BigDecimal> colRate;
    @FXML private TableColumn<PurchaseOrderRequest.PurchaseOrderItemDTO, BigDecimal> colTax;
    @FXML private TableColumn<PurchaseOrderRequest.PurchaseOrderItemDTO, BigDecimal> colTotalPrice;

    @FXML private Label grandTotalLabel;
    @FXML private Label selectedPoLabel;
    @FXML private ComboBox<PurchaseOrder.PoStatus> statusComboBox;
    @FXML private TableView<PurchaseOrder> allOrdersTable;
    @FXML private TableColumn<PurchaseOrder, String> colAllPoNumber;
    @FXML private TableColumn<PurchaseOrder, String> colAllVendor;
    @FXML private TableColumn<PurchaseOrder, LocalDate> colAllOrderDate;
    @FXML private TableColumn<PurchaseOrder, BigDecimal> colAllTotal;
    @FXML private TableColumn<PurchaseOrder, String> colAllStatus;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupItemEntryListeners();
        setupTables();
        statusComboBox.setItems(FXCollections.observableArrayList(PurchaseOrder.PoStatus.values()));
        setupAllOrdersTableListener();
        loadInitialData();
    }

    private void setupComboBoxes() {
        vendorComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Vendor vendor) { return vendor != null ? vendor.getName() : ""; }
            @Override public Vendor fromString(String string) { return null; }
        });

        productComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(ProductCatalog product) { return product != null ? product.getName() : ""; }
            @Override public ProductCatalog fromString(String string) { return null; }
        });

        productComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, product) -> {
            if (product != null) {
                purityField.setText(product.getPurity());
                rateField.setText(product.getDefaultRate());
            }
        });
    }

    private void setupItemEntryListeners() {
        weightField.textProperty().addListener((obs, old, val) -> calculateTotalPrice());
        rateField.textProperty().addListener((obs, old, val) -> calculateTotalPrice());
        taxField.textProperty().addListener((obs, old, val) -> calculateTotalPrice());
    }

    private void setupTables() {
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPurity.setCellValueFactory(new PropertyValueFactory<>("purity"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("ratePerUnit"));
        colTax.setCellValueFactory(new PropertyValueFactory<>("taxPercent"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        poItemsTable.setItems(poItems);

        colAllPoNumber.setCellValueFactory(new PropertyValueFactory<>("poNumber"));
        colAllVendor.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getVendor().getName()));
        colAllOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colAllTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colAllStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupAllOrdersTableListener() {
        allOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedOrder = selection;
            if (selectedOrder != null) {
                selectedPoLabel.setText(selectedOrder.getPoNumber());
                poNumberField.setText(selectedOrder.getPoNumber());
                orderDatePicker.setValue(selectedOrder.getOrderDate());
                remarksField.setText(selectedOrder.getRemarks());
                vendorComboBox.getSelectionModel().select(selectedOrder.getVendor());
                poItems.setAll(selectedOrder.getItems().stream()
                        .map(this::convertToItemDTO)
                        .collect(Collectors.toList()));
                updateGrandTotal();
            } else {
                selectedPoLabel.setText("None");
            }
        });
    }

    private void loadInitialData() {
        apiService.getVendors().thenAccept(vendors -> Platform.runLater(() -> vendorComboBox.setItems(FXCollections.observableArrayList(vendors))));
        apiService.getProducts().thenAccept(products -> Platform.runLater(() -> productComboBox.setItems(FXCollections.observableArrayList(products))));
        loadAllPurchaseOrders();
    }

    private void loadAllPurchaseOrders() {
        apiService.getAllPurchaseOrders().thenAccept(orders -> Platform.runLater(() -> {
            allOrdersTable.setItems(FXCollections.observableArrayList(orders));
            handleClearForm();
        }));
    }

    private void calculateTotalPrice() {
        try {
            BigDecimal weight = new BigDecimal(weightField.getText());
            BigDecimal rate = new BigDecimal(rateField.getText());
            BigDecimal tax = new BigDecimal(taxField.getText());

            BigDecimal subTotal = weight.multiply(rate);
            BigDecimal taxAmount = subTotal.multiply(tax.divide(new BigDecimal("100")));
            BigDecimal total = subTotal.add(taxAmount);

            totalPriceField.setText(total.setScale(2, RoundingMode.HALF_UP).toPlainString());
        } catch (Exception e) {
            totalPriceField.setText("");
        }
    }

    @FXML
    private void handleAddItem() {
        if (productComboBox.getSelectionModel().isEmpty() || weightField.getText().isBlank() || rateField.getText().isBlank() || taxField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Product, Weight, Rate, and Tax must be filled.").show();
            return;
        }
        try {
            PurchaseOrderRequest.PurchaseOrderItemDTO item = new PurchaseOrderRequest.PurchaseOrderItemDTO();
            item.setProductName(productComboBox.getSelectionModel().getSelectedItem().getName());
            item.setPurity(purityField.getText());
            item.setWeight(new BigDecimal(weightField.getText()));
            item.setRatePerUnit(new BigDecimal(rateField.getText()));
            item.setTaxPercent(new BigDecimal(taxField.getText()));
            item.setTotalPrice(new BigDecimal(totalPriceField.getText()));
            poItems.add(item);
            updateGrandTotal();
            clearItemEntry();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Please ensure all item fields have valid numbers.").show();
        }
    }

    private void updateGrandTotal() {
        BigDecimal total = poItems.stream()
                .map(PurchaseOrderRequest.PurchaseOrderItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        grandTotalLabel.setText(total.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    private void clearItemEntry() {
        productComboBox.getSelectionModel().clearSelection();
        purityField.clear();
        weightField.clear();
        rateField.clear();
        taxField.clear();
        totalPriceField.clear();
        productComboBox.requestFocus();
    }

    @FXML
    private void handleSaveOrder() {
        if (vendorComboBox.getSelectionModel().isEmpty() || poNumberField.getText().isBlank() || orderDatePicker.getValue() == null || poItems.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "PO Number, Order Date, and Vendor must be selected, and at least one item must be added.").show();
            return;
        }

        PurchaseOrderRequest request = new PurchaseOrderRequest();
        request.setPoNumber(poNumberField.getText());
        request.setVendorId(vendorComboBox.getSelectionModel().getSelectedItem().getId());
        request.setOrderDate(orderDatePicker.getValue());
        request.setRemarks(remarksField.getText());
        request.setTotalAmount(new BigDecimal(grandTotalLabel.getText()));
        request.setItems(poItems.stream().collect(Collectors.toList()));

        if (selectedOrder == null) {
            apiService.createPurchaseOrder(request).thenAccept(savedPo -> {
                Platform.runLater(this::loadAllPurchaseOrders);
            }).exceptionally(ex -> {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to save order: " + ex.getMessage()).show());
                return null;
            });
        } else {
            apiService.updatePurchaseOrder(selectedOrder.getId(), request).thenAccept(updatedPo -> {
                Platform.runLater(this::loadAllPurchaseOrders);
            }).exceptionally(ex -> {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to update order: " + ex.getMessage()).show());
                return null;
            });
        }
    }

    @FXML
    private void handleClearForm() {
        selectedOrder = null;
        poNumberField.clear();
        orderDatePicker.setValue(null);
        vendorComboBox.getSelectionModel().clearSelection();
        remarksField.clear();
        poItems.clear();
        updateGrandTotal();
        clearItemEntry();
        allOrdersTable.getSelectionModel().clearSelection();
        selectedPoLabel.setText("None");
        poNumberField.requestFocus();
    }

    @FXML
    private void handleUpdateStatus() {
        if (selectedOrder == null || statusComboBox.getSelectionModel().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please select an order and a new status.").show();
            return;
        }
        apiService.updatePurchaseOrderStatus(selectedOrder.getId(), statusComboBox.getValue())
                .thenAccept(updatedPo -> Platform.runLater(this::loadAllPurchaseOrders));
    }

    @FXML
    private void handleDeleteOrder() {
        if (selectedOrder == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an order to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedOrder.getPoNumber() + "?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deletePurchaseOrder(selectedOrder.getId())
                        .thenRun(() -> Platform.runLater(this::loadAllPurchaseOrders));
            }
        });
    }

    private PurchaseOrderRequest.PurchaseOrderItemDTO convertToItemDTO(PurchaseOrderItem item) {
        PurchaseOrderRequest.PurchaseOrderItemDTO dto = new PurchaseOrderRequest.PurchaseOrderItemDTO();
        dto.setProductName(item.getProductName());
        dto.setPurity(item.getPurity());
        dto.setWeight(item.getWeight());
        dto.setRatePerUnit(item.getRatePerUnit());
        dto.setTaxPercent(item.getTaxPercent());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }
}