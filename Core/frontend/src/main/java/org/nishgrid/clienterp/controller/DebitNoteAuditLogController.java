package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nishgrid.clienterp.dto.DebitNoteAuditLogResponse;
import org.nishgrid.clienterp.service.ApiService;

import java.time.LocalDateTime;

public class DebitNoteAuditLogController {

    private final ApiService apiService = new ApiService();

    @FXML private TableView<DebitNoteAuditLogResponse> logTable;
    @FXML private TableColumn<DebitNoteAuditLogResponse, LocalDateTime> colTimestamp;
    @FXML private TableColumn<DebitNoteAuditLogResponse, String> colDebitNote;
    @FXML private TableColumn<DebitNoteAuditLogResponse, String> colAction;
    @FXML private TableColumn<DebitNoteAuditLogResponse, String> colChangedBy;
    @FXML private TableColumn<DebitNoteAuditLogResponse, String> colOldValue;
    @FXML private TableColumn<DebitNoteAuditLogResponse, String> colNewValue;

    @FXML
    public void initialize() {
        setupTable();
        loadLogs();
    }

    private void setupTable() {
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("changedAt"));

        // This is the FIX: Add a null check to safely access the nested property
        colDebitNote.setCellValueFactory(cd -> {
            if (cd.getValue() != null && cd.getValue().getDebitNote() != null) {
                return new javafx.beans.property.SimpleStringProperty(cd.getValue().getDebitNote().getDebitNoteNo());
            }
            return new javafx.beans.property.SimpleStringProperty(""); // Return empty string if null
        });

        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colChangedBy.setCellValueFactory(new PropertyValueFactory<>("changedBy"));
        colOldValue.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        colNewValue.setCellValueFactory(new PropertyValueFactory<>("newValue"));
    }

    private void loadLogs() {
        apiService.getDebitNoteAuditLogs().thenAccept(logs ->
                Platform.runLater(() -> logTable.setItems(FXCollections.observableArrayList(logs)))
        );
    }
}