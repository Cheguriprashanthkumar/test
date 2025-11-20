package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.nishgrid.clienterp.dto.BankDetailsRequest;
import org.nishgrid.clienterp.dto.BankDetailsResponse;
import org.nishgrid.clienterp.model.BankDetails;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class BankDetailsController {

    private final ApiService apiService = new ApiService();
    private BankDetailsResponse selectedBankDetails = null;
    private File selectedFile = null;

    @FXML private TextField accountNameField;
    @FXML private TextField bankNameField;
    @FXML private TextField branchNameField;
    @FXML private TextField accountNumberField;
    @FXML private TextField ifscCodeField;
    @FXML private ComboBox<BankDetails.Status> statusComboBox;
    @FXML private Button chooseFileButton;
    @FXML private Label selectedFileLabel;
    @FXML private ImageView qrCodeImageView;

    @FXML private TableView<BankDetailsResponse> bankDetailsTable;
    @FXML private TableColumn<BankDetailsResponse, String> colBankName;
    @FXML private TableColumn<BankDetailsResponse, String> colAccountName;
    @FXML private TableColumn<BankDetailsResponse, String> colAccountNumber;
    @FXML private TableColumn<BankDetailsResponse, BankDetails.Status> colStatus;

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(BankDetails.Status.values()));
        setupTable();
        setupTableSelectionListener();
        loadInitialData();
    }

    private void setupTable() {
        colBankName.setCellValueFactory(new PropertyValueFactory<>("bankName"));
        colAccountName.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        colAccountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupTableSelectionListener() {
        bankDetailsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedBankDetails = selection;
            if (selection != null) {
                accountNameField.setText(selection.getAccountName());
                bankNameField.setText(selection.getBankName());
                branchNameField.setText(selection.getBranchName());
                accountNumberField.setText(selection.getAccountNumber());
                ifscCodeField.setText(selection.getIfscCode());
                statusComboBox.setValue(selection.getStatus());

                if (selection.getQrCodeData() != null) {
                    try {
                        byte[] qrCodeBytes = Base64.getDecoder().decode(selection.getQrCodeData());
                        Image image = new Image(new ByteArrayInputStream(qrCodeBytes));
                        qrCodeImageView.setImage(image);
                        selectedFileLabel.setText("QR Code available");
                    } catch (IllegalArgumentException e) {
                        qrCodeImageView.setImage(null);
                        selectedFileLabel.setText("Invalid QR Code data");
                    }
                } else {
                    qrCodeImageView.setImage(null);
                    selectedFileLabel.setText("No QR Code available");
                }

                selectedFile = null;
            }
        });
    }

    private void loadInitialData() {
        apiService.getAllBankDetails().thenAccept(data -> Platform.runLater(() -> bankDetailsTable.setItems(FXCollections.observableArrayList(data))));
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select QR Code Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (selectedFile != null) {
            selectedFileLabel.setText(selectedFile.getName());
            qrCodeImageView.setImage(new Image(selectedFile.toURI().toString()));
        } else {
            qrCodeImageView.setImage(null);
            selectedFileLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleSave() {
        BankDetailsRequest request = new BankDetailsRequest();
        request.setAccountName(accountNameField.getText());
        request.setBankName(bankNameField.getText());
        request.setBranchName(branchNameField.getText());
        request.setAccountNumber(accountNumberField.getText());
        request.setIfscCode(ifscCodeField.getText());
        request.setStatus(statusComboBox.getValue());

        if (selectedBankDetails == null) {
            apiService.createBankDetails(request, selectedFile).thenRun(() -> Platform.runLater(this::refreshData));
        } else {
            apiService.updateBankDetails(selectedBankDetails.getBankId(), request, selectedFile).thenRun(() -> Platform.runLater(this::refreshData));
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedBankDetails == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an account to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete this bank account record?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteBankDetails(selectedBankDetails.getBankId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        selectedBankDetails = null;
        selectedFile = null;
        bankDetailsTable.getSelectionModel().clearSelection();
        accountNameField.clear();
        bankNameField.clear();
        branchNameField.clear();
        accountNumberField.clear();
        ifscCodeField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        selectedFileLabel.setText("No file selected");
        qrCodeImageView.setImage(null);
    }

    private void refreshData() {
        loadInitialData();
        handleClearForm();
    }
}