package org.nishgrid.clienterp.controller;
import org.nishgrid.clienterp.service.ApiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nishgrid.clienterp.model.AttendanceLogFx;
import org.nishgrid.clienterp.model.EmployeeFx; // Assuming you have this model

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceViewController {

    @FXML private TableView<AttendanceLogFx> attendanceTable;
    @FXML private TableColumn<AttendanceLogFx, String> employeeCodeCol;
    @FXML private TableColumn<AttendanceLogFx, String> employeeNameCol;
    @FXML private TableColumn<AttendanceLogFx, LocalDate> dateCol;
    @FXML private TableColumn<AttendanceLogFx, LocalTime> checkInCol;
    @FXML private TableColumn<AttendanceLogFx, LocalTime> checkOutCol;
    @FXML private TableColumn<AttendanceLogFx, String> statusCol;
    @FXML private TableColumn<AttendanceLogFx, BigDecimal> hoursCol;
    @FXML private TextField searchField;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @FXML
    public void initialize() {
        // --- THIS IS THE KEY CHANGE ---
        // Use a lambda for nested properties. This tells the column how to get the
        // Employee Code from the nested EmployeeFx object.
        employeeCodeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployee().getEmployeeCode()));

        // Do the same for Employee Name, using the getFullName() method from your EmployeeFx class.
        employeeNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployee().getFullName()));

        // These can remain as they are, assuming they are direct properties of AttendanceLogFx
        dateCol.setCellValueFactory(new PropertyValueFactory<>("attendanceDate"));
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkInTime"));
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutTime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("presentStatus"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("workingHours"));

        loadInitialData();
    }

    @FXML
    private void loadInitialData() {
        fetchAttendanceData(ApiService.getBaseUrl()+"/attendance");
    }

    @FXML
    private void handleSearch() {
        String employeeCode = searchField.getText().trim();
        if (employeeCode.isEmpty()) {
            loadInitialData(); // Reload all data if search is cleared
            return;
        }
        fetchAttendanceData(ApiService.getBaseUrl()+"/attendance/employee/" + employeeCode);
    }

    private void fetchAttendanceData(String urlString) {
        Task<ObservableList<AttendanceLogFx>> task = new Task<>() {
            @Override
            protected ObservableList<AttendanceLogFx> call() throws Exception {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    // Read the error stream for a more detailed message from the server
                    try (BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                        String errorLine;
                        StringBuilder errorResponse = new StringBuilder();
                        while ((errorLine = err.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        throw new RuntimeException("Failed: HTTP Code " + conn.getResponseCode() + ", Response: " + errorResponse);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed: HTTP Code " + conn.getResponseCode());
                    }
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    // The TypeReference tells Jackson to expect a List of AttendanceLogFx objects
                    List<AttendanceLogFx> logs = objectMapper.readValue(br, new TypeReference<>() {});
                    return FXCollections.observableArrayList(logs);
                }
            }
        };

        task.setOnSucceeded(e -> attendanceTable.setItems(task.getValue()));
        task.setOnFailed(e -> {
            e.getSource().getException().printStackTrace(); // For developer debugging
            showAlert("Error", "Failed to fetch data: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Use ERROR for failures
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}