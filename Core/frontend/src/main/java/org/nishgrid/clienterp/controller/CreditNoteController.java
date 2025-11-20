package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.*;
import org.nishgrid.clienterp.model.CreditNote;
import org.nishgrid.clienterp.model.Customer;
import org.nishgrid.clienterp.model.ProductCatalog;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CreditNoteController {

    private final ApiService apiService = new ApiService();
    private CreditNoteResponse selectedCreditNote = null;
    private final ObservableList<CreditNoteRequest.CreditNoteItemDTO> currentItems = FXCollections.observableArrayList();

    // --- FXML Fields ---
    @FXML private TextField cnNumberField, reasonField, issuedByField, approvedByField;
    @FXML private DatePicker cnDatePicker;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<SalesInvoiceResponse> invoiceComboBox;
    @FXML private ComboBox<CreditNote.CreditNoteStatus> statusComboBox;
    @FXML private ComboBox<ProductCatalog> productComboBox;
    @FXML private TextField weightField, quantityField, rateField, discountField, taxRateField;
    @FXML private TableView<CreditNoteRequest.CreditNoteItemDTO> itemsTable;
    @FXML private TableColumn<CreditNoteRequest.CreditNoteItemDTO, String> colItemName;
    @FXML private TableColumn<CreditNoteRequest.CreditNoteItemDTO, BigDecimal> colWeight;
    @FXML private TableColumn<CreditNoteRequest.CreditNoteItemDTO, Integer> colQuantity;
    @FXML private TableColumn<CreditNoteRequest.CreditNoteItemDTO, BigDecimal> colRate;
    @FXML private TableColumn<CreditNoteRequest.CreditNoteItemDTO, BigDecimal> colDiscount;
    @FXML private TableColumn<CreditNoteRequest.CreditNoteItemDTO, BigDecimal> colTaxRate;
    @FXML private TableView<CreditNoteResponse.CreditNoteTaxResponse> taxTable;
    @FXML private TableColumn<CreditNoteResponse.CreditNoteTaxResponse, String> colTaxType;
    @FXML private TableColumn<CreditNoteResponse.CreditNoteTaxResponse, BigDecimal> colTaxRateDisplay;
    @FXML private TableColumn<CreditNoteResponse.CreditNoteTaxResponse, BigDecimal> colTaxAmount;
    @FXML private TableView<CreditNoteResponse> creditNoteTable;
    @FXML private TableColumn<CreditNoteResponse, LocalDate> colCnDate;
    @FXML private TableColumn<CreditNoteResponse, String> colCnNumber;
    @FXML private TableColumn<CreditNoteResponse, String> colCustomer;
    @FXML private TableColumn<CreditNoteResponse, String> colReason;
    @FXML private TableColumn<CreditNoteResponse, BigDecimal> colTotal;
    @FXML private TableColumn<CreditNoteResponse, CreditNote.CreditNoteStatus> colStatus;
    @FXML private Button addPaymentButton;
    @FXML private TableView<CreditNoteResponse.CreditNotePaymentResponse> paymentsTable;
    @FXML private TableColumn<CreditNoteResponse.CreditNotePaymentResponse, LocalDate> colPaymentDate;
    @FXML private TableColumn<CreditNoteResponse.CreditNotePaymentResponse, String> colSettlementType;
    @FXML private TableColumn<CreditNoteResponse.CreditNotePaymentResponse, BigDecimal> colPaymentAmount;
    @FXML private TableColumn<CreditNoteResponse.CreditNotePaymentResponse, String> colReference;
    @FXML private Button uploadFileButton;
    @FXML private TableView<CreditNoteFileResponse> filesTable;

    // --- Updated FXML Fields for Files Table ---
    @FXML private TableColumn<CreditNoteFileResponse, String> colFilePath; // Changed from colFileName
    @FXML private TableColumn<CreditNoteFileResponse, String> colFileType;
    @FXML private TableColumn<CreditNoteFileResponse, LocalDateTime> colUploadedAt;
    // Removed colFileSize and colUploadedBy

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTableColumns();
        setupTableSelectionListener();
        loadInitialData();
        addPaymentButton.setDisable(true);
        uploadFileButton.setDisable(true);
    }

    private void setupComboBoxes() {
        statusComboBox.setItems(FXCollections.observableArrayList(CreditNote.CreditNoteStatus.values()));
        customerComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Customer c) { return c != null ? c.getName() : ""; }
            @Override public Customer fromString(String s) { return null; }
        });
        invoiceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(SalesInvoiceResponse i) { return i != null ? i.getInvoiceNo() : ""; }
            @Override public SalesInvoiceResponse fromString(String s) { return null; }
        });
        productComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(ProductCatalog p) { return p != null ? p.getName() : ""; }
            @Override public ProductCatalog fromString(String s) { return null; }
        });
    }

    private void setupTableColumns() {
        // Main Credit Note Table
        colCnDate.setCellValueFactory(new PropertyValueFactory<>("creditNoteDate"));
        colCnNumber.setCellValueFactory(new PropertyValueFactory<>("creditNoteNumber"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmountIncludingTax"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Items Table
        colItemName.setCellValueFactory(cd -> {
            long productId = cd.getValue().getProductId();
            String name = productComboBox.getItems().stream()
                    .filter(p -> p.getId() == productId)
                    .map(ProductCatalog::getName).findFirst().orElse("N/A");
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("ratePerGram"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discountAmount"));
        colTaxRate.setCellValueFactory(new PropertyValueFactory<>("taxRate"));
        itemsTable.setItems(currentItems);

        // Tax Table
        colTaxType.setCellValueFactory(new PropertyValueFactory<>("taxType"));
        colTaxRateDisplay.setCellValueFactory(new PropertyValueFactory<>("taxRate"));
        colTaxAmount.setCellValueFactory(new PropertyValueFactory<>("taxAmount"));

        // Payments Table
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("settlementDate"));
        colSettlementType.setCellValueFactory(new PropertyValueFactory<>("settlementType"));
        colPaymentAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("referenceNumber"));

        // --- Updated Files Table Setup ---
        colFilePath.setCellValueFactory(new PropertyValueFactory<>("filePath")); // Changed from "fileName"
        colFileType.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        colUploadedAt.setCellValueFactory(new PropertyValueFactory<>("uploadedAt"));
        // Cell factories for fileSize and uploadedBy have been removed
    }

    private void setupTableSelectionListener() {
        creditNoteTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedCreditNote = selection;
            addPaymentButton.setDisable(selection == null);
            uploadFileButton.setDisable(selection == null);

            if (selection != null) {
                cnNumberField.setText(selection.getCreditNoteNumber());
                cnDatePicker.setValue(selection.getCreditNoteDate());
                reasonField.setText(selection.getReason());
                statusComboBox.setValue(selection.getStatus());
                issuedByField.setText(selection.getIssuedBy());
                approvedByField.setText(selection.getApprovedBy());

                if (selection.getCustomerId() != null) {
                    customerComboBox.getItems().stream()
                            .filter(c -> c.getCustomerId().equals(selection.getCustomerId())).findFirst()
                            .ifPresent(customerComboBox::setValue);
                }
                if (selection.getOriginalInvoiceId() != null) {
                    invoiceComboBox.getItems().stream()
                            .filter(i -> i.getInvoiceId().equals(selection.getOriginalInvoiceId())).findFirst()
                            .ifPresent(invoiceComboBox::setValue);
                } else {
                    invoiceComboBox.getSelectionModel().clearSelection();
                }

                currentItems.setAll(selection.getItems().stream().map(this::convertItemResponseToRequest).collect(Collectors.toList()));
                taxTable.setItems(FXCollections.observableArrayList(selection.getTaxes()));
                paymentsTable.setItems(FXCollections.observableArrayList(selection.getPayments()));

                // This method will now correctly load data into the updated filesTable
                loadFilesForCreditNote(selection.getCreditNoteId());
            } else {
                handleClearForm();
            }
        });
    }

    private CreditNoteRequest.CreditNoteItemDTO convertItemResponseToRequest(CreditNoteResponse.CreditNoteItemResponse resp) {
        CreditNoteRequest.CreditNoteItemDTO req = new CreditNoteRequest.CreditNoteItemDTO();
        req.setProductId(resp.getProductId());
        req.setDescription(resp.getProductName());
        req.setHsnCode(resp.getHsnCode());
        req.setPurity(resp.getPurity());
        req.setWeight(resp.getWeight());
        req.setQuantity(resp.getQuantity());
        req.setRatePerGram(resp.getRatePerGram());
        req.setDiscountAmount(resp.getDiscountAmount());
        req.setTaxRate(resp.getTaxRate());
        return req;
    }

    private void loadInitialData() {
        CompletableFuture<List<Customer>> customerFuture = apiService.getCustomers();
        CompletableFuture<List<SalesInvoiceResponse>> invoiceFuture = apiService.getAllSalesInvoices();
        CompletableFuture<List<ProductCatalog>> productFuture = apiService.getProducts();

        CompletableFuture.allOf(customerFuture, invoiceFuture, productFuture).thenRun(() -> {
            Platform.runLater(() -> {
                customerComboBox.setItems(FXCollections.observableArrayList(customerFuture.join()));
                invoiceComboBox.setItems(FXCollections.observableArrayList(invoiceFuture.join()));
                productComboBox.setItems(FXCollections.observableArrayList(productFuture.join()));
                loadAllCreditNotes();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to load initial data: " + ex.getMessage()).show());
            return null;
        });
    }

    private void loadAllCreditNotes() {
        apiService.getAllCreditNotes()
                .thenAccept(data -> Platform.runLater(() -> creditNoteTable.setItems(FXCollections.observableArrayList(data))))
                .exceptionally(ex -> {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to load credit notes: " + ex.getMessage()).show());
                    return null;
                });
    }

    private void loadFilesForCreditNote(Long creditNoteId) {
        if (creditNoteId == null) {
            filesTable.getItems().clear();
            return;
        }
        apiService.getFilesForCreditNote(creditNoteId)
                .thenAccept(files -> Platform.runLater(() -> {
                    filesTable.setItems(FXCollections.observableArrayList(files));
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to load files: " + ex.getMessage()).show());
                    return null;
                });
    }
    @FXML
    private void handleUploadFile() {
        if (selectedCreditNote == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a Credit Note before uploading a file.").show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File file = fileChooser.showOpenDialog(uploadFileButton.getScene().getWindow());

        if (file != null) {
            // REMOVED: The 'uploadedBy' variable is no longer needed.
            // String uploadedBy = "currentUser";

            // UPDATED: Call to ApiService no longer includes 'uploadedBy'.
            apiService.uploadCreditNoteFile(selectedCreditNote.getCreditNoteId(), file)
                    .thenAccept(uploadedFile -> Platform.runLater(() -> {
                        loadFilesForCreditNote(selectedCreditNote.getCreditNoteId());
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "File upload failed: " + ex.getMessage()).show());
                        return null;
                    });
        }
    }
    @FXML
    private void handleAddItem() {
        if (productComboBox.getValue() == null || quantityField.getText().isBlank() || rateField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Product, Quantity, and Rate are required.").show();
            return;
        }
        try {
            CreditNoteRequest.CreditNoteItemDTO item = new CreditNoteRequest.CreditNoteItemDTO();
            item.setProductId(productComboBox.getValue().getId());
            item.setWeight(new BigDecimal(weightField.getText()));
            item.setQuantity(Integer.parseInt(quantityField.getText()));
            item.setRatePerGram(new BigDecimal(rateField.getText()));
            item.setDiscountAmount(!discountField.getText().isBlank() ? new BigDecimal(discountField.getText()) : BigDecimal.ZERO);
            item.setTaxRate(!taxRateField.getText().isBlank() ? new BigDecimal(taxRateField.getText()) : BigDecimal.ZERO);
            currentItems.add(item);
            clearItemForm();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter valid numbers for weight, quantity, rate, etc.").show();
        }
    }

    private void clearItemForm() {
        productComboBox.getSelectionModel().clearSelection();
        weightField.clear();
        quantityField.clear();
        rateField.clear();
        discountField.clear();
        taxRateField.clear();
    }

    @FXML
    private void handleSave() {
        if (customerComboBox.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "A Customer must be selected.").show();
            return;
        }

        CreditNoteRequest request = new CreditNoteRequest();
        request.setCreditNoteDate(cnDatePicker.getValue());
        request.setCustomerId(customerComboBox.getValue().getCustomerId());
        if (invoiceComboBox.getValue() != null) {
            request.setOriginalInvoiceId(invoiceComboBox.getValue().getInvoiceId());
        }
        request.setReason(reasonField.getText());
        request.setStatus(statusComboBox.getValue());
        request.setIssuedBy(issuedByField.getText());
        request.setApprovedBy(approvedByField.getText());
        request.setItems(new ArrayList<>(currentItems));

        CompletableFuture<CreditNoteResponse> future;
        if (selectedCreditNote == null) {
            future = apiService.createCreditNote(request);
        } else {
            future = apiService.updateCreditNote(selectedCreditNote.getCreditNoteId(), request);
        }

        future.thenRun(() -> Platform.runLater(this::refreshData))
                .exceptionally(ex -> {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to save credit note: " + ex.getMessage()).show());
                    return null;
                });
    }

    @FXML
    private void handleDelete() {
        if (selectedCreditNote == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a credit note to delete.").show();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedCreditNote.getCreditNoteNumber() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteCreditNote(selectedCreditNote.getCreditNoteId())
                        .thenRun(() -> Platform.runLater(this::refreshData))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to delete: " + ex.getMessage()).show());
                            return null;
                        });
            }
        });
    }

    @FXML
    private void handleAddPayment() {
        if (selectedCreditNote == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a Credit Note to add a payment.").show();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddPaymentDialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Payment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addPaymentButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AddPaymentDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                CreditNotePaymentRequest paymentRequest = controller.getPaymentResult();
                apiService.addPayment(selectedCreditNote.getCreditNoteId(), paymentRequest)
                        .thenRun(() -> Platform.runLater(this::refreshData))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to add payment: " + ex.getMessage()).show());
                            return null;
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open the payment dialog.").show();
        }
    }



    @FXML
    private void handleClearForm() {
        selectedCreditNote = null;
        creditNoteTable.getSelectionModel().clearSelection();
        cnNumberField.clear();
        cnDatePicker.setValue(null);
        customerComboBox.getSelectionModel().clearSelection();
        invoiceComboBox.getSelectionModel().clearSelection();
        reasonField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        issuedByField.clear();
        approvedByField.clear();

        currentItems.clear();
        taxTable.getItems().clear();
        paymentsTable.getItems().clear();
        filesTable.getItems().clear();
        clearItemForm();
    }

    private void refreshData() {
        loadAllCreditNotes();
        handleClearForm();
    }
}