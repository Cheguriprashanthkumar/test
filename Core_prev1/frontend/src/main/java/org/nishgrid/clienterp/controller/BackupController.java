package org.nishgrid.clienterp.controller;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.nishgrid.clienterp.MainApp;
import org.nishgrid.clienterp.model.BackupLog;
import org.nishgrid.clienterp.service.ApiService;
import org.nishgrid.clienterp.service.ApiService;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

public class BackupController {

    private final ApiService apiService = new ApiService();
    private final HostServices hostServices = MainApp.getAppHostServices();

    @FXML private Button importSqlButton;
    @FXML private TableView<BackupLog> logTable;
    @FXML private TableColumn<BackupLog, LocalDateTime> colTimestamp;
    @FXML private TableColumn<BackupLog, String> colAction;
    @FXML private TableColumn<BackupLog, String> colFileName;
    @FXML private TableColumn<BackupLog, String> colStatus;
    @FXML private TableColumn<BackupLog, String> colUser;
    @FXML private TableColumn<BackupLog, String> colRemarks;

    @FXML
    public void initialize() {
        setupTable();
        loadLogs();
    }

    private void setupTable() {
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("performedAt"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("performedBy"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
    }


    private void loadLogs() {
        apiService.getBackupLogs().thenAccept(logs ->
                Platform.runLater(() -> {
                    logTable.getItems().clear();
                    logTable.setItems(FXCollections.observableArrayList(logs));
                })
        );
    }


    @FXML
    private void handleExportSql() {
        if (hostServices != null) {
            hostServices.showDocument(ApiService.getBaseUrl()+"/backup/export/sql");
            refreshLogsAfterAction();
        }
    }

    @FXML
    private void handleExportExcel() {
        if (hostServices != null) {
            hostServices.showDocument(ApiService.getBaseUrl()+"/backup/export/excel");
            refreshLogsAfterAction();
        }
    }

    @FXML
    private void handleImportSql() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Restore");
        confirmation.setHeaderText("This will overwrite all existing data.");
        confirmation.setContentText("This action cannot be undone. Are you sure you want to proceed?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select SQL Backup File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
            File selectedFile = fileChooser.showOpenDialog(importSqlButton.getScene().getWindow());

            if (selectedFile != null) {
                apiService.importSqlBackup(selectedFile).thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            new Alert(Alert.AlertType.INFORMATION, "Database restored successfully!").show();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Restore failed: " + response.body()).show();
                        }
                        loadLogs();
                    });
                });
            }
        }
    }

    private void refreshLogsAfterAction() {
        // Add a small delay to give the time to write the log entry
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> loadLogs());
            }
        }, 2000); // 2-second delay
    }
}