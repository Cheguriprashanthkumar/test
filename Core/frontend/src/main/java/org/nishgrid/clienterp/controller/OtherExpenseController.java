package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.nishgrid.clienterp.dto.CancelExpenseRequest;
import org.nishgrid.clienterp.dto.OtherExpenseRequest;
import org.nishgrid.clienterp.dto.OtherExpenseResponse;
import org.nishgrid.clienterp.model.OtherExpense;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class OtherExpenseController {

    private final ApiService apiService = new ApiService();
    private OtherExpenseResponse selectedExpense = null;
    private File selectedFile = null;

    @FXML private DatePicker expenseDatePicker;
    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private TextField paidToField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<OtherExpense.PaymentMode> paymentModeComboBox;
    @FXML private TextField referenceField;
    @FXML private ComboBox<OtherExpense.ApprovalStatus> approvalStatusComboBox;
    @FXML private CheckBox gstClaimableCheckBox;
    @FXML private TextField gstPercentField;
    @FXML private TextField gstAmountField;
    @FXML private TextField billNumberField;
    @FXML private TextField branchLocationField;
    @FXML private ComboBox<OtherExpense.ExpenseType> expenseTypeComboBox;
    @FXML private DatePicker nextDueDatePicker;
    @FXML private TextField remarksField;
    @FXML private TextField createdByField;
    @FXML private TextField approvedByField;
    @FXML private Button chooseFileButton;
    @FXML private Label selectedFileLabel;

    @FXML private TableView<OtherExpenseResponse> expenseTable;
    @FXML private TableColumn<OtherExpenseResponse, LocalDate> colDate;
    @FXML private TableColumn<OtherExpenseResponse, String> colCategory;
    @FXML private TableColumn<OtherExpenseResponse, String> colPaidTo;
    @FXML private TableColumn<OtherExpenseResponse, BigDecimal> colAmount;
    @FXML private TableColumn<OtherExpenseResponse, String> colBillNumber;
    @FXML private TableColumn<OtherExpenseResponse, OtherExpense.PaymentMode> colPaymentMode;
    @FXML private TableColumn<OtherExpenseResponse, String> colBranch;
    @FXML private TableColumn<OtherExpenseResponse, String> colCreatedBy;
    @FXML private TableColumn<OtherExpenseResponse, String> colApprovedBy;
    @FXML private TableColumn<OtherExpenseResponse, OtherExpense.ApprovalStatus> colStatus;

    @FXML
    public void initialize() {
        paymentModeComboBox.setItems(FXCollections.observableArrayList(OtherExpense.PaymentMode.values()));
        approvalStatusComboBox.setItems(FXCollections.observableArrayList(OtherExpense.ApprovalStatus.values()));
        expenseTypeComboBox.setItems(FXCollections.observableArrayList(OtherExpense.ExpenseType.values()));

        setupTable();
        setupTableSelectionListener();
        loadInitialData();
    }

    private void setupTable() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("expenseDate"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("expenseCategory"));
        colPaidTo.setCellValueFactory(new PropertyValueFactory<>("paidTo"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));
        colBillNumber.setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        colPaymentMode.setCellValueFactory(new PropertyValueFactory<>("paymentMode"));
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branchLocation"));
        colCreatedBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        colApprovedBy.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));
    }

    private void setupTableSelectionListener() {
        expenseTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selection) -> {
            selectedExpense = selection;
            if (selection != null) {
                expenseDatePicker.setValue(selection.getExpenseDate());
                categoryField.setText(selection.getExpenseCategory());
                amountField.setText(selection.getAmount() != null ? selection.getAmount().toPlainString() : "");
                paidToField.setText(selection.getPaidTo());
                descriptionArea.setText(selection.getExpenseDescription());
                paymentModeComboBox.setValue(selection.getPaymentMode());
                referenceField.setText(selection.getReferenceNumber());
                approvalStatusComboBox.setValue(selection.getApprovalStatus());
                gstClaimableCheckBox.setSelected(selection.getIsGstClaimable() != null && selection.getIsGstClaimable());
                gstPercentField.setText(selection.getGstPercent() != null ? selection.getGstPercent().toPlainString() : "");
                gstAmountField.setText(selection.getGstAmount() != null ? selection.getGstAmount().toPlainString() : "");
                billNumberField.setText(selection.getBillNumber());
                branchLocationField.setText(selection.getBranchLocation());
                expenseTypeComboBox.setValue(selection.getExpenseType());
                nextDueDatePicker.setValue(selection.getNextDueDate());
                remarksField.setText(selection.getRemarks());
                createdByField.setText(selection.getCreatedBy());
                approvedByField.setText(selection.getApprovedBy());
                selectedFileLabel.setText(selection.getBillAttachment() != null ? selection.getBillAttachment() : "No file selected");
            }
        });
    }

    private void loadInitialData() {
        apiService.getAllExpenses().thenAccept(expenses -> Platform.runLater(() -> expenseTable.setItems(FXCollections.observableArrayList(expenses))));
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Bill/Receipt");
        selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (selectedFile != null) {
            selectedFileLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleSaveExpense() {
        if (categoryField.getText().isBlank() || amountField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Category and Amount are required.").show();
            return;
        }

        OtherExpenseRequest request = new OtherExpenseRequest();
        request.setExpenseDate(expenseDatePicker.getValue());
        request.setExpenseCategory(categoryField.getText());
        request.setAmount(new BigDecimal(amountField.getText()));
        request.setPaidTo(paidToField.getText());
        request.setExpenseDescription(descriptionArea.getText());
        request.setPaymentMode(paymentModeComboBox.getValue());
        request.setReferenceNumber(referenceField.getText());
        request.setApprovalStatus(approvalStatusComboBox.getValue());
        request.setIsGstClaimable(gstClaimableCheckBox.isSelected());
        request.setGstPercent(!gstPercentField.getText().isBlank() ? new BigDecimal(gstPercentField.getText()) : null);
        request.setGstAmount(!gstAmountField.getText().isBlank() ? new BigDecimal(gstAmountField.getText()) : null);
        request.setBillNumber(billNumberField.getText());
        request.setBranchLocation(branchLocationField.getText());
        request.setExpenseType(expenseTypeComboBox.getValue());
        request.setNextDueDate(nextDueDatePicker.getValue());
        request.setRemarks(remarksField.getText());
        request.setCreatedBy(createdByField.getText());
        request.setApprovedBy(approvedByField.getText());

        if (selectedExpense == null) {
            apiService.createExpense(request, selectedFile).thenRun(() -> Platform.runLater(this::refreshData));
        } else {
            apiService.updateExpense(selectedExpense.getExpenseId(), request, selectedFile).thenRun(() -> Platform.runLater(this::refreshData));
        }
    }

    @FXML
    private void handleCancelExpense() {
        if (selectedExpense == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an expense to cancel.").show();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancel Expense");
        dialog.setHeaderText("Cancelling Expense: " + selectedExpense.getExpenseCategory());
        dialog.setContentText("Please enter the reason for cancellation:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (!reason.isBlank()) {
                CancelExpenseRequest request = new CancelExpenseRequest();
                request.setCancelledBy("admin");
                request.setCancelReason(reason);

                apiService.cancelExpense(selectedExpense.getExpenseId(), request)
                        .thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleDeleteExpense() {
        if (selectedExpense == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an expense to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete this expense record?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteExpense(selectedExpense.getExpenseId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        selectedExpense = null;
        selectedFile = null;
        expenseTable.getSelectionModel().clearSelection();
        expenseDatePicker.setValue(null);
        categoryField.clear();
        amountField.clear();
        paidToField.clear();
        descriptionArea.clear();
        paymentModeComboBox.getSelectionModel().clearSelection();
        referenceField.clear();
        approvalStatusComboBox.getSelectionModel().clearSelection();
        gstClaimableCheckBox.setSelected(false);
        gstPercentField.clear();
        gstAmountField.clear();
        billNumberField.clear();
        branchLocationField.clear();
        expenseTypeComboBox.getSelectionModel().clearSelection();
        nextDueDatePicker.setValue(null);
        remarksField.clear();
        createdByField.clear();
        approvedByField.clear();
        selectedFileLabel.setText("No file selected");
    }

    private void refreshData() {
        loadInitialData();
        handleClearForm();
    }
}