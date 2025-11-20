package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.PurchaseInvoiceRequest;
import org.nishgrid.clienterp.dto.PurchaseInvoiceResponse;
import org.nishgrid.clienterp.model.PurchaseOrder;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseInvoiceController {

    private final ApiService apiService = new ApiService();
    private PurchaseInvoiceResponse selectedInvoice = null;

    @FXML private ComboBox<PurchaseOrder> poComboBox;
    @FXML private TextField vendorField;
    @FXML private TextField invoiceNumberField;
    @FXML private DatePicker invoiceDatePicker;
    @FXML private TextField totalAmountField;
    @FXML private TextField gstAmountField;
    @FXML private TextField grandTotalField;

    @FXML private TableView<PurchaseInvoiceResponse> invoiceTable;
    @FXML private TableColumn<PurchaseInvoiceResponse, String> colInvoiceNumber;
    @FXML private TableColumn<PurchaseInvoiceResponse, String> colPoNumber;
    @FXML private TableColumn<PurchaseInvoiceResponse, String> colVendor;
    @FXML private TableColumn<PurchaseInvoiceResponse, LocalDate> colInvoiceDate;
    @FXML private TableColumn<PurchaseInvoiceResponse, BigDecimal> colGrandTotal;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        setupAmountListeners();
        setupTableSelectionListener();
        loadInitialData();
    }

    private void setupComboBox() {
        poComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(PurchaseOrder po) { return po != null ? po.getPoNumber() : ""; }
            @Override public PurchaseOrder fromString(String s) { return null; }
        });

        poComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, po) -> {
            if (po != null) {
                vendorField.setText(po.getVendor().getName());
                totalAmountField.setText(po.getTotalAmount().toPlainString());
            }
        });
    }

    private void setupTable() {
        colInvoiceNumber.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        colPoNumber.setCellValueFactory(new PropertyValueFactory<>("poNumber"));
        colVendor.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        colInvoiceDate.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        colGrandTotal.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));
    }

    private void setupAmountListeners() {
        totalAmountField.textProperty().addListener((obs, old, val) -> calculateGrandTotal());
        gstAmountField.textProperty().addListener((obs, old, val) -> calculateGrandTotal());
    }

    private void setupTableSelectionListener() {
        invoiceTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedInvoice = selection;
            if (selection != null) {
                poComboBox.setDisable(true);
                invoiceNumberField.setText(selection.getInvoiceNumber());
                invoiceDatePicker.setValue(selection.getInvoiceDate());
                totalAmountField.setText(selection.getTotalAmount().toPlainString());
                gstAmountField.setText(selection.getGstAmount().toPlainString());
                // Find and select the matching PO in the ComboBox
                poComboBox.getItems().stream()
                        .filter(po -> po.getId() == selection.getPurchaseOrderId())
                        .findFirst().ifPresent(po -> poComboBox.getSelectionModel().select(po));
            }
        });
    }

    private void loadInitialData() {
        apiService.getAllPurchaseOrders().thenAccept(allPOs -> {
            List<PurchaseOrder> receivedPOs = allPOs.stream()
                    .filter(po -> "RECEIVED".equalsIgnoreCase(po.getStatus()))
                    .collect(Collectors.toList());
            Platform.runLater(() -> poComboBox.setItems(FXCollections.observableArrayList(receivedPOs)));
        });
        loadAllInvoices();
    }

    private void loadAllInvoices() {
        apiService.getAllInvoices().thenAccept(invoices -> Platform.runLater(() -> invoiceTable.setItems(FXCollections.observableArrayList(invoices))));
    }

    private void calculateGrandTotal() {
        try {
            BigDecimal total = new BigDecimal(totalAmountField.getText());
            BigDecimal gst = new BigDecimal(gstAmountField.getText());
            grandTotalField.setText(total.add(gst).setScale(2, RoundingMode.HALF_UP).toPlainString());
        } catch (Exception e) {
            grandTotalField.clear();
        }
    }

    @FXML
    private void handleSaveInvoice() {
        if (poComboBox.getSelectionModel().isEmpty() || invoiceNumberField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "A Purchase Order and Invoice Number are required.").show();
            return;
        }

        PurchaseInvoiceRequest request = new PurchaseInvoiceRequest();
        request.setInvoiceNumber(invoiceNumberField.getText());
        request.setPurchaseOrderId(poComboBox.getSelectionModel().getSelectedItem().getId());
        request.setVendorId(poComboBox.getSelectionModel().getSelectedItem().getVendor().getId());
        request.setInvoiceDate(invoiceDatePicker.getValue());
        request.setTotalAmount(new BigDecimal(totalAmountField.getText()));
        request.setGstAmount(new BigDecimal(gstAmountField.getText()));
        request.setGrandTotal(new BigDecimal(grandTotalField.getText()));

        if (selectedInvoice == null) {
            apiService.createInvoice(request).thenRun(() -> Platform.runLater(this::refreshData));
        } else {
            apiService.updateInvoice(selectedInvoice.getId(), request).thenRun(() -> Platform.runLater(this::refreshData));
        }
    }

    @FXML
    private void handleDeleteInvoice() {
        if (selectedInvoice == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an invoice to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete invoice " + selectedInvoice.getInvoiceNumber() + "?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteInvoice(selectedInvoice.getId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        selectedInvoice = null;
        invoiceTable.getSelectionModel().clearSelection();
        poComboBox.getSelectionModel().clearSelection();
        vendorField.clear();
        invoiceNumberField.clear();
        invoiceDatePicker.setValue(null);
        totalAmountField.clear();
        gstAmountField.clear();
        grandTotalField.clear();
        poComboBox.setDisable(false);
    }

    private void refreshData() {
        loadInitialData();
        handleClearForm();
    }
}