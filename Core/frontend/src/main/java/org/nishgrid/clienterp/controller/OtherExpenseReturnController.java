package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.OtherExpenseResponse;
import org.nishgrid.clienterp.dto.OtherExpenseReturnRequest;
import org.nishgrid.clienterp.dto.OtherExpenseReturnResponse;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OtherExpenseReturnController {

    private final ApiService apiService = new ApiService();
    private OtherExpenseReturnResponse selectedReturn = null;
    private File selectedFile = null;

    @FXML private ComboBox<OtherExpenseResponse> expenseComboBox;
    @FXML private DatePicker returnDatePicker;
    @FXML private TextField amountReturnedField;
    @FXML private TextField refundModeField;
    @FXML private TextField referenceField;
    @FXML private TextField returnedByField;
    @FXML private TextField receivedByField;
    @FXML private TextField approvedByField;
    @FXML private TextArea reasonArea;
    @FXML private Button chooseFileButton;
    @FXML private Label selectedFileLabel;

    @FXML private TableView<OtherExpenseReturnResponse> returnTable;
    @FXML private TableColumn<OtherExpenseReturnResponse, LocalDate> colReturnDate;
    @FXML private TableColumn<OtherExpenseReturnResponse, String> colExpenseCategory;
    @FXML private TableColumn<OtherExpenseReturnResponse, BigDecimal> colReturnedAmount;
    @FXML private TableColumn<OtherExpenseReturnResponse, String> colReturnedBy;
    @FXML private TableColumn<OtherExpenseReturnResponse, String> colReceivedBy;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        setupTableSelectionListener();
        loadInitialData();
    }

    private void setupComboBox() {
        expenseComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(OtherExpenseResponse exp) { return exp != null ? exp.getExpenseCategory() + " (" + exp.getPaidTo() + ")" : ""; }
            @Override public OtherExpenseResponse fromString(String s) { return null; }
        });

        expenseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, exp) -> {
            if (exp != null) {
                returnedByField.setText(exp.getPaidTo());
                amountReturnedField.setText(exp.getAmount().toPlainString());
            }
        });
    }

    private void setupTable() {
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colExpenseCategory.setCellValueFactory(new PropertyValueFactory<>("expenseCategory"));
        colReturnedAmount.setCellValueFactory(new PropertyValueFactory<>("returnedAmount"));
        colReturnedBy.setCellValueFactory(new PropertyValueFactory<>("returnedBy"));
        colReceivedBy.setCellValueFactory(new PropertyValueFactory<>("receivedBy"));
    }

    private void setupTableSelectionListener() {
        returnTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedReturn = selection;
            if (selection != null) {
                expenseComboBox.setDisable(true);
                returnDatePicker.setValue(selection.getReturnDate());
                amountReturnedField.setText(selection.getReturnedAmount().toPlainString());
                refundModeField.setText(selection.getRefundMode());
                referenceField.setText(selection.getRefundReferenceNo());
                returnedByField.setText(selection.getReturnedBy());
                receivedByField.setText(selection.getReceivedBy());
                approvedByField.setText(selection.getApprovedBy());
                reasonArea.setText(selection.getReturnReason());
                selectedFileLabel.setText(selection.getAttachmentPath() != null ? selection.getAttachmentPath() : "No file selected");
                expenseComboBox.getItems().stream()
                        .filter(exp -> exp.getExpenseId().equals(selection.getExpenseId()))
                        .findFirst().ifPresent(exp -> expenseComboBox.getSelectionModel().select(exp));
            }
        });
    }

    private void loadInitialData() {
        apiService.getAllExpenses().thenAccept(expenses -> Platform.runLater(() -> expenseComboBox.setItems(FXCollections.observableArrayList(expenses))));
        loadAllReturns();
    }

    private void loadAllReturns() {
        apiService.getAllExpenseReturns().thenAccept(returns -> Platform.runLater(() -> returnTable.setItems(FXCollections.observableArrayList(returns))));
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (selectedFile != null) {
            selectedFileLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleSaveReturn() {
        if (expenseComboBox.getSelectionModel().isEmpty() || amountReturnedField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Original Expense and Amount Returned are required.").show();
            return;
        }

        OtherExpenseReturnRequest request = new OtherExpenseReturnRequest();
        request.setExpenseId(expenseComboBox.getSelectionModel().getSelectedItem().getExpenseId());
        request.setReturnDate(returnDatePicker.getValue());
        request.setReturnedAmount(new BigDecimal(amountReturnedField.getText()));
        request.setRefundMode(refundModeField.getText());
        request.setRefundReferenceNo(referenceField.getText());
        request.setReturnedBy(returnedByField.getText());
        request.setReturnReason(reasonArea.getText());
        request.setReceivedBy(receivedByField.getText());
        request.setApprovedBy(approvedByField.getText());

        if (selectedReturn == null) {
            apiService.createExpenseReturn(request, selectedFile).thenRun(() -> Platform.runLater(this::refreshData));
        } else {
            apiService.updateExpenseReturn(selectedReturn.getReturnId(), request, selectedFile).thenRun(() -> Platform.runLater(this::refreshData));
        }
    }

    @FXML
    private void handleDeleteReturn() {
        if (selectedReturn == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a return to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete this return record?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteExpenseReturn(selectedReturn.getReturnId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        selectedReturn = null;
        selectedFile = null;
        returnTable.getSelectionModel().clearSelection();
        expenseComboBox.getSelectionModel().clearSelection();
        returnDatePicker.setValue(null);
        amountReturnedField.clear();
        refundModeField.clear();
        referenceField.clear();
        returnedByField.clear();
        receivedByField.clear();
        approvedByField.clear();
        reasonArea.clear();
        selectedFileLabel.setText("No file selected");
        expenseComboBox.setDisable(false);
    }

    private void refreshData() {
        loadAllReturns();
        handleClearForm();
    }
}