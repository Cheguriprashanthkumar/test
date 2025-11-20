package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nishgrid.clienterp.dto.OtherExpenseResponse;
import org.nishgrid.clienterp.dto.ReportRequest;
import org.nishgrid.clienterp.model.OtherExpense;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class ExpenseReportController {

    private final ApiService apiService = new ApiService();

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<OtherExpenseResponse> reportTable;
    @FXML private Label totalAmountLabel;

    @FXML private TableColumn<OtherExpenseResponse, LocalDate> colDate;
    @FXML private TableColumn<OtherExpenseResponse, String> colCategory;
    @FXML private TableColumn<OtherExpenseResponse, String> colPaidTo;
    @FXML private TableColumn<OtherExpenseResponse, BigDecimal> colAmount;
    @FXML private TableColumn<OtherExpenseResponse, String> colBillNumber;
    @FXML private TableColumn<OtherExpenseResponse, OtherExpense.ApprovalStatus> colStatus;

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("expenseDate"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("expenseCategory"));
        colPaidTo.setCellValueFactory(new PropertyValueFactory<>("paidTo"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colBillNumber.setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));
    }

    @FXML
    private void handleGenerateReport() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select both a start and end date.").show();
            return;
        }

        ReportRequest request = new ReportRequest();
        request.setStartDate(startDatePicker.getValue());
        request.setEndDate(endDatePicker.getValue());

        apiService.generateExpenseReport(request).thenAccept(expenses ->
                Platform.runLater(() -> {
                    reportTable.setItems(FXCollections.observableArrayList(expenses));
                    calculateTotal(expenses);
                })
        );
    }

    private void calculateTotal(List<OtherExpenseResponse> expenses) {
        BigDecimal total = expenses.stream()
                .map(OtherExpenseResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAmountLabel.setText(total.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    @FXML
    private void handleClear() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        reportTable.getItems().clear();
        totalAmountLabel.setText("0.00");
    }
}