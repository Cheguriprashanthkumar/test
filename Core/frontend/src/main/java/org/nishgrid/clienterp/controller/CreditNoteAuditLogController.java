package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nishgrid.clienterp.dto.CreditNoteAuditLogResponse;
import org.nishgrid.clienterp.service.ApiService;

import java.time.LocalDateTime;
import java.util.List;

public class CreditNoteAuditLogController {

    private final ApiService apiService = new ApiService();

    @FXML private TableView<CreditNoteAuditLogResponse> auditLogTable;
    @FXML private TableColumn<CreditNoteAuditLogResponse, Long> colLogId;
    @FXML private TableColumn<CreditNoteAuditLogResponse, Long> colCreditNoteId;
    @FXML private TableColumn<CreditNoteAuditLogResponse, LocalDateTime> colChangedAt;
    @FXML private TableColumn<CreditNoteAuditLogResponse, String> colChangedBy;
    @FXML private TableColumn<CreditNoteAuditLogResponse, String> colDescription;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAuditLogs();
    }

    private void setupTableColumns() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colCreditNoteId.setCellValueFactory(new PropertyValueFactory<>("creditNoteId"));
        colChangedAt.setCellValueFactory(new PropertyValueFactory<>("changedAt"));
        colChangedBy.setCellValueFactory(new PropertyValueFactory<>("changedBy"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("changeDescription"));
    }

    private void loadAuditLogs() {
        apiService.getAllCreditNoteAuditLogs()
                .thenAccept(data -> Platform.runLater(() -> auditLogTable.setItems(FXCollections.observableArrayList(data))))
                .exceptionally(ex -> {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to load audit logs: " + ex.getMessage()).show());
                    return null;
                });
    }
}