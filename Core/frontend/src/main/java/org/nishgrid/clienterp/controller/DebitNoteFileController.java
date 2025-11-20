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
import org.nishgrid.clienterp.dto.DebitNoteFileResponse;
import org.nishgrid.clienterp.dto.DebitNoteResponse;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;

public class DebitNoteFileController {

    private final ApiService apiService = new ApiService();
    private File selectedFile;

    @FXML private ComboBox<DebitNoteResponse> debitNoteComboBox;
    @FXML private Button chooseFileButton;
    @FXML private Label selectedFileLabel;
    @FXML private Label documentListLabel;
    @FXML private TableView<DebitNoteFileResponse> documentTable;
    @FXML private TableColumn<DebitNoteFileResponse, String> colFileName;
    @FXML private TableColumn<DebitNoteFileResponse, String> colFileType;
    @FXML private TableColumn<DebitNoteFileResponse, String> colUploadedBy;

    private HostServices hostServices = MainApp.getAppHostServices();

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        loadInitialData();
    }

    private void setupComboBox() {
        debitNoteComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(DebitNoteResponse dn) { return dn != null ? dn.getDebitNoteNo() : ""; }
            @Override public DebitNoteResponse fromString(String s) { return null; }
        });

        debitNoteComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            if (selection != null) {
                documentListLabel.setText("Documents for Debit Note: " + selection.getDebitNoteNo());
                loadDocumentsForDebitNote(selection.getDebitNoteId());
            } else {
                documentListLabel.setText("Documents for Selected Debit Note");
                documentTable.getItems().clear();
            }
        });
    }

    private void setupTable() {
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colFileType.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        colUploadedBy.setCellValueFactory(new PropertyValueFactory<>("uploadedBy"));
    }

    private void loadInitialData() {
        apiService.getAllDebitNotes().thenAccept(notes -> Platform.runLater(() -> debitNoteComboBox.setItems(FXCollections.observableArrayList(notes))));
    }

    private void loadDocumentsForDebitNote(long debitNoteId) {
        apiService.getFilesForDebitNote(debitNoteId)
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
        if (selectedFile == null || debitNoteComboBox.getSelectionModel().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please select a debit note and a file to upload.").show();
            return;
        }
        long debitNoteId = debitNoteComboBox.getSelectionModel().getSelectedItem().getDebitNoteId();
        apiService.uploadDebitNoteFile(debitNoteId, selectedFile, "admin")
                .thenRun(() -> Platform.runLater(() -> {
                    loadDocumentsForDebitNote(debitNoteId);
                    selectedFile = null;
                    selectedFileLabel.setText("No file selected");
                }));
    }

    @FXML
    private void handleDownload() {
        DebitNoteFileResponse selectedDoc = documentTable.getSelectionModel().getSelectedItem();
        if (selectedDoc != null && hostServices != null) {
            hostServices.showDocument(selectedDoc.getFileDownloadUri());
        }
    }

    @FXML
    private void handleDelete() {
        DebitNoteFileResponse selectedDoc = documentTable.getSelectionModel().getSelectedItem();
        if (selectedDoc == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a document to delete.").show();
            return;
        }
        apiService.deleteDebitNoteFile(selectedDoc.getId())
                .thenRun(() -> Platform.runLater(() -> loadDocumentsForDebitNote(selectedDoc.getDebitNoteId())));
    }
}