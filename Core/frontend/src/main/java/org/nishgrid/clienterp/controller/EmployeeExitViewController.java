package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.model.EmployeeExit;
import org.nishgrid.clienterp.service.ApiService;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeExitViewController {

    @FXML private TableView<EmployeeExit> exitedEmployeesTable;
    @FXML private TableColumn<EmployeeExit, String> employeeCodeColumn;
    @FXML private TableColumn<EmployeeExit, String> employeeNameColumn;
    @FXML private TableColumn<EmployeeExit, LocalDate> exitDateColumn;
    @FXML private TableColumn<EmployeeExit, String> exitReasonColumn;
    @FXML private TableColumn<EmployeeExit, Number> settlementColumn;
    @FXML private TableColumn<EmployeeExit, String> statusColumn;
    @FXML private Button refreshButton;
    @FXML private Label listStatusLabel;

    // This is injected from the <fx:include> tag in the FXML
    @FXML private EmployeeExitFormController employeeExitFormController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    public void initialize() {
        // Register module to handle LocalDate from JSON
        objectMapper.registerModule(new JavaTimeModule());

        setupTableColumns();
        refreshButton.setOnAction(e -> fetchExitedEmployees());

        // Initial data load
        fetchExitedEmployees();
    }

    private void setupTableColumns() {
        // For nested object property 'employee.employeeCode'
        employeeCodeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployee().getEmployeeCode())
        );
        // For nested object property 'employee.fullName'
        employeeNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployee().getFullName())
        );
        exitDateColumn.setCellValueFactory(cellData -> cellData.getValue().exitDateProperty());
        exitReasonColumn.setCellValueFactory(cellData -> cellData.getValue().reasonProperty());
        settlementColumn.setCellValueFactory(cellData -> cellData.getValue().settlementProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().clearanceStatusProperty());
    }

    @FXML
    private void handleRefresh() {
        fetchExitedEmployees();
    }

    private void fetchExitedEmployees() {
        listStatusLabel.setStyle("-fx-text-fill: black;");
        listStatusLabel.setText("Fetching data...");

        new Thread(() -> {
            try {
                URL url = new URL(ApiService.getBaseUrl()+"/exits");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream in = conn.getInputStream()) {
                        // Use TypeReference to deserialize a list of objects
                        List<EmployeeExit> exits = objectMapper.readValue(in, new TypeReference<List<EmployeeExit>>() {});

                        Platform.runLater(() -> {
                            exitedEmployeesTable.setItems(FXCollections.observableArrayList(exits));
                            listStatusLabel.setStyle("-fx-text-fill: green;");
                            listStatusLabel.setText("Data loaded successfully.");
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        listStatusLabel.setStyle("-fx-text-fill: red;");
                        listStatusLabel.setText("Error: Failed to fetch data (Code: " + responseCode + ")");
                        showAlert(Alert.AlertType.ERROR, "Fetch Error", "Could not retrieve employee exit list.");
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    listStatusLabel.setStyle("-fx-text-fill: red;");
                    listStatusLabel.setText("Error: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Network Error", "An error occurred while fetching data: " + e.getMessage());
                });
            }
        }).start();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}