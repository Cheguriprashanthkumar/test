package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nishgrid.clienterp.dto.CancelledExpenseLogResponse;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CancelledExpenseLogController {

    private final ApiService apiService = new ApiService();

    @FXML private TableView<CancelledExpenseLogResponse> logTable;
    @FXML private TableColumn<CancelledExpenseLogResponse, LocalDateTime> colCancelledOn;
    @FXML private TableColumn<CancelledExpenseLogResponse, Long> colExpenseId;
    @FXML private TableColumn<CancelledExpenseLogResponse, String> colCategory;
    @FXML private TableColumn<CancelledExpenseLogResponse, BigDecimal> colOldAmount;
    @FXML private TableColumn<CancelledExpenseLogResponse, String> colReason;
    @FXML private TableColumn<CancelledExpenseLogResponse, String> colCancelledBy;

    @FXML
    public void initialize() {
        setupTable();
        loadLogs();
    }

    private void setupTable() {
        colCancelledOn.setCellValueFactory(new PropertyValueFactory<>("cancelledOn"));
        colExpenseId.setCellValueFactory(new PropertyValueFactory<>("expenseId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("expenseCategory"));
        colOldAmount.setCellValueFactory(new PropertyValueFactory<>("oldAmount"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("cancelReason"));
        colCancelledBy.setCellValueFactory(new PropertyValueFactory<>("cancelledBy"));
    }

    private void loadLogs() {
        apiService.getCancelledExpenseLogs().thenAccept(logs ->
                Platform.runLater(() -> logTable.setItems(FXCollections.observableArrayList(logs)))
        );
    }
}