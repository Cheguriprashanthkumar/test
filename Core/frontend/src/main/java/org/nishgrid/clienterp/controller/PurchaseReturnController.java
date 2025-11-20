package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.PurchaseInvoiceResponse;
import org.nishgrid.clienterp.dto.PurchaseReturnRequest;
import org.nishgrid.clienterp.dto.PurchaseReturnResponse;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseReturnController {

    private final ApiService apiService = new ApiService();
    private PurchaseReturnResponse selectedReturn = null;

    @FXML private TextField returnNumberField;
    @FXML private DatePicker returnDatePicker;
    @FXML private ComboBox<PurchaseInvoiceResponse> invoiceComboBox;
    @FXML private TextField vendorField;
    @FXML private TextField amountReturnedField;
    @FXML private TextArea reasonArea;

    @FXML private TableView<PurchaseReturnResponse> returnTable;
    @FXML private TableColumn<PurchaseReturnResponse, String> colReturnNumber;
    @FXML private TableColumn<PurchaseReturnResponse, String> colInvoiceNumber;
    @FXML private TableColumn<PurchaseReturnResponse, String> colVendor;
    @FXML private TableColumn<PurchaseReturnResponse, LocalDate> colReturnDate;
    @FXML private TableColumn<PurchaseReturnResponse, BigDecimal> colAmountReturned;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        setupTableSelectionListener();
        loadInitialData();
    }

    private void setupComboBox() {
        invoiceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(PurchaseInvoiceResponse inv) { return inv != null ? inv.getInvoiceNumber() : ""; }
            @Override public PurchaseInvoiceResponse fromString(String s) { return null; }
        });

        invoiceComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, inv) -> {
            if (inv != null) {
                vendorField.setText(inv.getVendorName());
            } else {
                vendorField.clear();
            }
        });
    }

    private void setupTable() {
        colReturnNumber.setCellValueFactory(new PropertyValueFactory<>("returnNumber"));
        colInvoiceNumber.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        colVendor.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colAmountReturned.setCellValueFactory(new PropertyValueFactory<>("amountReturned"));
    }

    private void setupTableSelectionListener() {
        returnTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedReturn = selection;
            if (selection != null) {
                returnNumberField.setText(selection.getReturnNumber());
                returnDatePicker.setValue(selection.getReturnDate());
                amountReturnedField.setText(selection.getAmountReturned().toPlainString());
                reasonArea.setText(selection.getReason());
                invoiceComboBox.setDisable(true);
                // Find and select the matching invoice in the ComboBox
                invoiceComboBox.getItems().stream()
                        .filter(inv -> inv.getId().equals(selection.getPurchaseInvoiceId()))
                        .findFirst().ifPresent(inv -> invoiceComboBox.getSelectionModel().select(inv));
            }
        });
    }

    private void loadInitialData() {
        apiService.getAllInvoices().thenAccept(invoices -> Platform.runLater(() -> invoiceComboBox.setItems(FXCollections.observableArrayList(invoices))));
        loadAllReturns();
    }

    private void loadAllReturns() {
        apiService.getAllReturns().thenAccept(returns -> Platform.runLater(() -> returnTable.setItems(FXCollections.observableArrayList(returns))));
    }

    @FXML
    private void handleSaveReturn() {
        if (invoiceComboBox.getSelectionModel().isEmpty() || returnNumberField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "An Invoice and Return Number are required.").show();
            return;
        }

        PurchaseReturnRequest request = new PurchaseReturnRequest();
        request.setReturnNumber(returnNumberField.getText());
        request.setPurchaseInvoiceId(invoiceComboBox.getSelectionModel().getSelectedItem().getId());
        request.setVendorId(invoiceComboBox.getSelectionModel().getSelectedItem().getVendorId());
        request.setReturnDate(returnDatePicker.getValue());
        request.setReason(reasonArea.getText());
        request.setAmountReturned(new BigDecimal(amountReturnedField.getText()));

        if (selectedReturn == null) {
            apiService.createReturn(request).thenRun(() -> Platform.runLater(this::refreshData));
        } else {
            apiService.updateReturn(selectedReturn.getId(), request).thenRun(() -> Platform.runLater(this::refreshData));
        }
    }

    @FXML
    private void handleDeleteReturn() {
        if (selectedReturn == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a return to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete return " + selectedReturn.getReturnNumber() + "?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteReturn(selectedReturn.getId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        selectedReturn = null;
        returnTable.getSelectionModel().clearSelection();
        invoiceComboBox.getSelectionModel().clearSelection();
        vendorField.clear();

        returnNumberField.clear();
        returnDatePicker.setValue(null);
        amountReturnedField.clear();
        reasonArea.clear();
        invoiceComboBox.setDisable(false);
    }

    private void refreshData() {
        loadAllReturns();
        handleClearForm();
    }
}