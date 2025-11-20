package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.DebitNoteRequest;
import org.nishgrid.clienterp.dto.DebitNoteResponse;
import org.nishgrid.clienterp.dto.DebitNoteTaxResponse;
import org.nishgrid.clienterp.dto.PurchaseInvoiceResponse;
import org.nishgrid.clienterp.model.DebitNote;
import org.nishgrid.clienterp.model.ProductCatalog;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DebitNoteController {

    private final ApiService apiService = new ApiService();
    private final ObservableList<DebitNoteRequest.DebitNoteItemDTO> currentItems = FXCollections.observableArrayList();
    private DebitNoteResponse selectedDebitNote = null;

    // FXML Fields for main form
    @FXML private TextField debitNoteNumberField;
    @FXML private DatePicker debitNoteDatePicker;
    @FXML private ComboBox<Vendor> vendorComboBox;
    @FXML private ComboBox<PurchaseInvoiceResponse> invoiceComboBox;
    @FXML private TextField reasonField;
    @FXML private ComboBox<DebitNote.DebitNoteStatus> statusComboBox;
    @FXML private TextField createdByField;
    @FXML private TextField approvedByField;

    // FXML Fields for item entry
    @FXML private ComboBox<ProductCatalog> productComboBox;
    @FXML private TextField hsnField;
    @FXML private TextField purityField;
    @FXML private TextField weightField;
    @FXML private TextField qtyField;
    @FXML private TextField rateField;
    @FXML private TextField gstRateField;

    // FXML Fields for items table
    @FXML private TableView<DebitNoteRequest.DebitNoteItemDTO> itemsTable;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, String> colItemName;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, String> colHsn;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, String> colPurity;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, BigDecimal> colWeight;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, Integer> colQty;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, BigDecimal> colRate;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, BigDecimal> colGstRate;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, BigDecimal> colGstAmount;
    @FXML private TableColumn<DebitNoteRequest.DebitNoteItemDTO, BigDecimal> colLineTotal;

    // FXML Fields for debit note list table
    @FXML private TableView<DebitNoteResponse> debitNoteTable;
    @FXML private TableColumn<DebitNoteResponse, LocalDate> colDnDate;
    @FXML private TableColumn<DebitNoteResponse, String> colDnNumber;
    @FXML private TableColumn<DebitNoteResponse, String> colDnVendor;
    @FXML private TableColumn<DebitNoteResponse, String> colDnReason;
    @FXML private TableColumn<DebitNoteResponse, BigDecimal> colDnTotal;
    @FXML private TableColumn<DebitNoteResponse, DebitNote.DebitNoteStatus> colDnStatus;

    // FXML Fields for the new tax table
    @FXML private TableView<DebitNoteTaxResponse> taxTable;
    @FXML private TableColumn<DebitNoteTaxResponse, String> colTaxType;
    @FXML private TableColumn<DebitNoteTaxResponse, BigDecimal> colTaxRate;
    @FXML private TableColumn<DebitNoteTaxResponse, BigDecimal> colTaxAmount;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTables();
        setupTableSelectionListener();
        loadInitialData();
    }

    private void setupComboBoxes() {
        statusComboBox.setItems(FXCollections.observableArrayList(DebitNote.DebitNoteStatus.values()));
        vendorComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Vendor v) { return v != null ? v.getName() : ""; }
            @Override public Vendor fromString(String s) { return null; }
        });
        invoiceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(PurchaseInvoiceResponse i) { return i != null ? i.getInvoiceNumber() : ""; }
            @Override public PurchaseInvoiceResponse fromString(String s) { return null; }
        });
        productComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(ProductCatalog p) { return p != null ? p.getName() : ""; }
            @Override public ProductCatalog fromString(String s) { return null; }
        });
    }

    private void setupTables() {
        // Items Table Setup
        colItemName.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                productComboBox.getItems().stream()
                        .filter(p -> p.getId() == cd.getValue().getItemId())
                        .map(ProductCatalog::getName).findFirst().orElse("")));
        colHsn.setCellValueFactory(new PropertyValueFactory<>("hsnCode"));
        colPurity.setCellValueFactory(new PropertyValueFactory<>("purity"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("unitRate"));
        colGstRate.setCellValueFactory(new PropertyValueFactory<>("gstRate"));
        colGstAmount.setCellValueFactory(new PropertyValueFactory<>("gstAmount"));
        colLineTotal.setCellValueFactory(new PropertyValueFactory<>("totalWithGst"));
        itemsTable.setItems(currentItems);

        // Debit Note List Table Setup
        colDnDate.setCellValueFactory(new PropertyValueFactory<>("debitNoteDate"));
        colDnNumber.setCellValueFactory(new PropertyValueFactory<>("debitNoteNo"));
        colDnVendor.setCellValueFactory(new PropertyValueFactory<>("vendorName"));
        colDnReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colDnTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmountWithGst"));
        colDnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Tax Table Setup
        colTaxType.setCellValueFactory(new PropertyValueFactory<>("taxType"));
        colTaxRate.setCellValueFactory(new PropertyValueFactory<>("taxRate"));
        colTaxAmount.setCellValueFactory(new PropertyValueFactory<>("taxAmount"));
    }

    private void setupTableSelectionListener() {
        debitNoteTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedDebitNote = selection;
            if (selection != null) {
                // Populate form fields
                debitNoteNumberField.setText(selection.getDebitNoteNo());
                debitNoteDatePicker.setValue(selection.getDebitNoteDate());
                reasonField.setText(selection.getReason());
                statusComboBox.setValue(selection.getStatus());
                createdByField.setText(selection.getCreatedBy());
                approvedByField.setText(selection.getApprovedBy());
                vendorComboBox.getItems().stream().filter(v -> v.getId() == selection.getVendorId()).findFirst().ifPresent(vendorComboBox::setValue);
                invoiceComboBox.getItems().stream().filter(i -> {
                    // Handle potential null purchaseInvoiceId
                    return selection.getPurchaseInvoiceId() != null && selection.getPurchaseInvoiceId().equals(i.getId());
                }).findFirst().ifPresent(invoiceComboBox::setValue);

                // Populate the items table
                currentItems.setAll(selection.getItems().stream()
                        .map(this::convertItemResponseToRequest)
                        .collect(Collectors.toList()));

                // Populate the tax table from the selection object
                taxTable.setItems(FXCollections.observableArrayList(selection.getTaxes()));
            }
        });
    }

    private DebitNoteRequest.DebitNoteItemDTO convertItemResponseToRequest(DebitNoteResponse.DebitNoteItemResponse itemResp) {
        DebitNoteRequest.DebitNoteItemDTO itemDto = new DebitNoteRequest.DebitNoteItemDTO();
        itemDto.setItemId(itemResp.getItemId());
        itemDto.setHsnCode(itemResp.getHsnCode());
        itemDto.setPurity(itemResp.getPurity());
        itemDto.setWeight(itemResp.getWeight());
        itemDto.setQty(itemResp.getQty());
        itemDto.setUnitRate(itemResp.getUnitRate());
        itemDto.setGstRate(itemResp.getGstRate());
        itemDto.setGstAmount(itemResp.getGstAmount());
        itemDto.setLineTotal(itemResp.getLineTotal());
        itemDto.setTotalWithGst(itemResp.getTotalWithGst());
        return itemDto;
    }

    private void loadInitialData() {
        // You can add similar error handling to these calls as well
        apiService.getVendors().thenAccept(data -> Platform.runLater(() -> vendorComboBox.setItems(FXCollections.observableArrayList(data))));
        apiService.getAllInvoices().thenAccept(data -> Platform.runLater(() -> invoiceComboBox.setItems(FXCollections.observableArrayList(data))));
        apiService.getProducts().thenAccept(data -> Platform.runLater(() -> productComboBox.setItems(FXCollections.observableArrayList(data))));

        loadAllDebitNotes(); // This is the most important one for the table
    }

    private void loadAllDebitNotes() {
        apiService.getAllDebitNotes()
                .thenAccept(data -> Platform.runLater(() -> {
                    debitNoteTable.setItems(FXCollections.observableArrayList(data));
                    System.out.println("Successfully loaded " + data.size() + " debit notes.");
                }))
                // --- THIS IS THE CRUCIAL FIX ---
                .exceptionally(ex -> {
                    // This block will run if the API call fails for any reason
                    Platform.runLater(() -> {
                        System.err.println("Error loading debit notes: " + ex.getMessage());
                        ex.printStackTrace(); // This prints the full error to the console
                        new Alert(Alert.AlertType.ERROR, "Failed to load debit notes from the server.\nCheck the console for details.").show();
                    });
                    return null; // Required for exceptionally()
                });
    }

    @FXML
    private void handleAddItem() {
        if (productComboBox.getValue() == null || qtyField.getText().isBlank() || rateField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Product, Quantity, and Rate are required.").show();
            return;
        }

        try {
            DebitNoteRequest.DebitNoteItemDTO item = new DebitNoteRequest.DebitNoteItemDTO();
            item.setItemId(productComboBox.getValue().getId());
            item.setHsnCode(hsnField.getText());
            item.setPurity(purityField.getText());
            item.setWeight(new BigDecimal(weightField.getText()));
            item.setQty(Integer.parseInt(qtyField.getText()));
            item.setUnitRate(new BigDecimal(rateField.getText()));
            item.setGstRate(!gstRateField.getText().isBlank() ? new BigDecimal(gstRateField.getText()) : BigDecimal.ZERO);

            BigDecimal lineTotal = item.getUnitRate().multiply(new BigDecimal(item.getQty()));
            BigDecimal gstAmount = lineTotal.multiply(item.getGstRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            item.setLineTotal(lineTotal);
            item.setGstAmount(gstAmount);
            item.setTotalWithGst(lineTotal.add(gstAmount));

            currentItems.add(item);
            clearItemForm();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid number format in one of the item fields.").show();
        }
    }

    private void clearItemForm() {
        productComboBox.getSelectionModel().clearSelection();
        hsnField.clear();
        purityField.clear();
        weightField.clear();
        qtyField.clear();
        rateField.clear();
        gstRateField.clear();
    }

    @FXML
    private void handleSave() {
        if (vendorComboBox.getValue() == null || debitNoteNumberField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Debit Note # and Vendor are required.").show();
            return;
        }

        DebitNoteRequest request = new DebitNoteRequest();
        request.setDebitNoteNo(debitNoteNumberField.getText());
        request.setDebitNoteDate(debitNoteDatePicker.getValue());
        request.setVendorId(vendorComboBox.getValue().getId());
        if (invoiceComboBox.getValue() != null) {
            request.setPurchaseInvoiceId(invoiceComboBox.getValue().getId());
        }
        request.setReason(reasonField.getText());
        request.setStatus(statusComboBox.getValue());
        request.setCreatedBy(createdByField.getText());
        request.setApprovedBy(approvedByField.getText());
        request.setItems(new ArrayList<>(currentItems));

        if (selectedDebitNote == null) {
            apiService.createDebitNote(request).thenRun(() -> Platform.runLater(this::refreshData));
        } else {
            apiService.updateDebitNote(selectedDebitNote.getDebitNoteId(), request).thenRun(() -> Platform.runLater(this::refreshData));
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedDebitNote == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a debit note to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedDebitNote.getDebitNoteNo() + "?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteDebitNote(selectedDebitNote.getDebitNoteId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        selectedDebitNote = null;
        debitNoteTable.getSelectionModel().clearSelection();
        debitNoteNumberField.clear();
        debitNoteDatePicker.setValue(null);
        vendorComboBox.getSelectionModel().clearSelection();
        invoiceComboBox.getSelectionModel().clearSelection();
        reasonField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        createdByField.clear();
        approvedByField.clear();
        currentItems.clear();
        taxTable.getItems().clear();
        clearItemForm();
    }

    private void refreshData() {
        loadAllDebitNotes();
        handleClearForm();
    }
}