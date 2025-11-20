package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.VendorPaymentRequest;
import org.nishgrid.clienterp.dto.VendorPaymentResponse;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.model.VendorPayment;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VendorPaymentController {

    private final ApiService apiService = new ApiService();
    private VendorPaymentResponse selectedPayment = null;

    @FXML private ComboBox<Vendor> vendorComboBox;
    @FXML private DatePicker paymentDatePicker;
    @FXML private TextField amountPaidField;
    @FXML private ComboBox<VendorPayment.PaymentMode> paymentModeComboBox;
    @FXML private TextField referenceField;
    @FXML private TextField remarksField;

    @FXML private TableView<VendorPaymentResponse> paymentTable;
    @FXML private TableColumn<VendorPaymentResponse, String> colVendor;
    @FXML private TableColumn<VendorPaymentResponse, LocalDate> colDate;
    @FXML private TableColumn<VendorPaymentResponse, BigDecimal> colAmount;
    @FXML private TableColumn<VendorPaymentResponse, VendorPayment.PaymentMode> colMode;
    @FXML private TableColumn<VendorPaymentResponse, String> colReference;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTable();
        setupTableSelectionListener();
        loadInitialData();
    }

    private void setupComboBoxes() {
        vendorComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Vendor v) { return v != null ? v.getName() : ""; }
            @Override public Vendor fromString(String s) { return null; }
        });
        paymentModeComboBox.setItems(FXCollections.observableArrayList(VendorPayment.PaymentMode.values()));
    }

    private void setupTable() {
        colVendor.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        colMode.setCellValueFactory(new PropertyValueFactory<>("paymentMode"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("referenceNo"));
    }

    private void setupTableSelectionListener() {
        paymentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedPayment = selection;
            if (selection != null) {
                paymentDatePicker.setValue(selection.getPaymentDate());
                amountPaidField.setText(selection.getAmountPaid().toPlainString());
                paymentModeComboBox.getSelectionModel().select(selection.getPaymentMode());
                referenceField.setText(selection.getReferenceNo());
                remarksField.setText(selection.getRemarks());

                vendorComboBox.getItems().stream()
                        .filter(v -> v.getId() == selection.getVendorId())
                        .findFirst().ifPresent(v -> vendorComboBox.getSelectionModel().select(v));
            }
        });
    }

    private void loadInitialData() {
        apiService.getVendors().thenAccept(vendors -> Platform.runLater(() -> vendorComboBox.setItems(FXCollections.observableArrayList(vendors))));
        loadAllPayments();
    }



    private void loadAllPayments() {
        apiService.getAllPayments().thenAccept(payments -> Platform.runLater(() -> paymentTable.setItems(FXCollections.observableArrayList(payments))));
    }

    @FXML
    private void handleSavePayment() {
        if (vendorComboBox.getSelectionModel().isEmpty() || amountPaidField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Vendor and Amount Paid are required.").show();
            return;
        }

        VendorPaymentRequest request = new VendorPaymentRequest();
        request.setVendorId(vendorComboBox.getSelectionModel().getSelectedItem().getId());
        request.setPaymentDate(paymentDatePicker.getValue());
        request.setAmountPaid(new BigDecimal(amountPaidField.getText()));
        request.setPaymentMode(paymentModeComboBox.getSelectionModel().getSelectedItem());
        request.setReferenceNo(referenceField.getText());
        request.setRemarks(remarksField.getText());

        if (selectedPayment == null) {
            apiService.createPayment(request).thenRun(() -> Platform.runLater(this::refreshData));
        } else {
            apiService.updatePayment(selectedPayment.getId(), request).thenRun(() -> Platform.runLater(this::refreshData));
        }
    }

    @FXML
    private void handleDeletePayment() {
        if (selectedPayment == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a payment to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete this payment record?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deletePayment(selectedPayment.getId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        selectedPayment = null;
        paymentTable.getSelectionModel().clearSelection();
        vendorComboBox.getSelectionModel().clearSelection();
        paymentDatePicker.setValue(null);

        amountPaidField.clear();
        paymentModeComboBox.getSelectionModel().clearSelection();
        referenceField.clear();
        remarksField.clear();
    }

    private void refreshData() {
        loadAllPayments();
        handleClearForm();
    }
}