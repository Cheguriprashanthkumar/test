package org.nishgrid.clienterp.controller;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.MainApp;
import org.nishgrid.clienterp.dto.PurchaseDocumentResponse;
import org.nishgrid.clienterp.dto.PurchaseInvoiceResponse;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;

public class PurchaseDocumentController {

    private final ApiService apiService = new ApiService();
    private File selectedFile;

    @FXML private ComboBox<PurchaseInvoiceResponse> invoiceComboBox;
    @FXML private Button chooseFileButton;
    @FXML private Label selectedFileLabel;
    @FXML private Label documentListLabel;
    @FXML private TableView<PurchaseDocumentResponse> documentTable;
    @FXML private TableColumn<PurchaseDocumentResponse, String> colFileName;
    @FXML private TableColumn<PurchaseDocumentResponse, String> colFileType;

    private HostServices hostServices = MainApp.getAppHostServices();

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        loadInitialData();
    }

    private void setupComboBox() {
        invoiceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(PurchaseInvoiceResponse inv) { return inv != null ? inv.getInvoiceNumber() : ""; }
            @Override public PurchaseInvoiceResponse fromString(String s) { return null; }
        });

        invoiceComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            if (selection != null) {
                documentListLabel.setText("Documents for Invoice: " + selection.getInvoiceNumber());
                loadDocumentsForInvoice(selection.getId());
            } else {
                documentListLabel.setText("Documents for Selected Invoice");
                documentTable.getItems().clear();
            }
        });
    }

    private void setupTable() {
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colFileType.setCellValueFactory(new PropertyValueFactory<>("fileType"));
    }

    private void loadInitialData() {
        apiService.getAllInvoices().thenAccept(invoices -> Platform.runLater(() -> invoiceComboBox.setItems(FXCollections.observableArrayList(invoices))));
    }

    private void loadDocumentsForInvoice(long invoiceId) {
        apiService.getDocumentsForInvoice(invoiceId)
                .thenAccept(docs -> Platform.runLater(() -> documentTable.setItems(FXCollections.observableArrayList(docs))));
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Document");
        selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (selectedFile != null) {
            selectedFileLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleUpload() {
        if (selectedFile == null || invoiceComboBox.getSelectionModel().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please select an invoice and a file to upload.").show();
            return;
        }
        long invoiceId = invoiceComboBox.getSelectionModel().getSelectedItem().getId();
        apiService.uploadDocument(invoiceId, selectedFile, "admin")
                .thenRun(() -> Platform.runLater(() -> {
                    loadDocumentsForInvoice(invoiceId);
                    selectedFile = null;
                    selectedFileLabel.setText("No file selected");
                }));
    }

    @FXML
    private void handleReplace() {
        PurchaseDocumentResponse selectedDoc = documentTable.getSelectionModel().getSelectedItem();
        if (selectedDoc == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a document to replace.").show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select New Document");
        File newFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());

        if (newFile != null) {
            // First, delete the old document
            apiService.deleteDocument(selectedDoc.getId()).thenRun(() -> {
                // Then, upload the new one
                apiService.uploadDocument(selectedDoc.getPurchaseInvoiceId(), newFile, "admin")
                        .thenRun(() -> Platform.runLater(() -> loadDocumentsForInvoice(selectedDoc.getPurchaseInvoiceId())));
            });
        }
    }

    @FXML
    private void handleDownload() {
        PurchaseDocumentResponse selectedDoc = documentTable.getSelectionModel().getSelectedItem();
        if (selectedDoc != null && hostServices != null) {
            hostServices.showDocument(selectedDoc.getFileDownloadUri());
        }
    }

    @FXML
    private void handleDelete() {
        PurchaseDocumentResponse selectedDoc = documentTable.getSelectionModel().getSelectedItem();
        if (selectedDoc == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a document to delete.").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedDoc.getFileName() + "?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteDocument(selectedDoc.getId())
                        .thenRun(() -> Platform.runLater(() -> loadDocumentsForInvoice(selectedDoc.getPurchaseInvoiceId())));
            }
        });
    }
}