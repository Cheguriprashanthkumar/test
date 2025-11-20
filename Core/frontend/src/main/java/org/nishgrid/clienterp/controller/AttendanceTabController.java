package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.nishgrid.clienterp.service.ApiService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.AttendanceLogFx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class AttendanceTabController {

    @FXML private TableView<AttendanceLogFx> attendanceTable;
    @FXML private TableColumn<AttendanceLogFx, Integer> colLogId;
    @FXML private TableColumn<AttendanceLogFx, String> colEmpCode;
    @FXML private TableColumn<AttendanceLogFx, LocalDate> colDate;
    @FXML private TableColumn<AttendanceLogFx, String> colIn;
    @FXML private TableColumn<AttendanceLogFx, String> colOut;
    @FXML private TableColumn<AttendanceLogFx, String> colHours;
    @FXML private TableColumn<AttendanceLogFx, String> colStatus;
    @FXML private TextField searchEmployeeCode;
    @FXML private DatePicker searchDate;
    @FXML private TableColumn<AttendanceLogFx, Void> colEdit;
    private final ObservableList<AttendanceLogFx> allLogs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        loadAttendanceLogs();
    }



    private void setupColumns() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colEmpCode.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getEmployeeCode() != null ? cellData.getValue().getEmployeeCode() : ""));
        colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAttendanceDate()));
        colIn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCheckInTime() != null ? cellData.getValue().getCheckInTime().toString() : ""));
        colOut.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCheckOutTime() != null ? cellData.getValue().getCheckOutTime().toString() : ""));
        colHours.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getWorkingHours() != null ? cellData.getValue().getWorkingHours().toPlainString() : ""));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPresentStatus() != null ? cellData.getValue().getPresentStatus() : ""));

        // Set Edit Button Column
        colEdit.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Edit");

            {
                btn.setOnAction(event -> {
                    AttendanceLogFx data = getTableView().getItems().get(getIndex());
                    openEditWindow(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }


    private void loadAttendanceLogs() {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/attendance");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                in.close();

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                List<AttendanceLogFx> logs = mapper.readValue(
                        jsonBuilder.toString(),
                        new TypeReference<>() {}
                );

                allLogs.setAll(logs);
                attendanceTable.setItems(allLogs);

            } else {
                showAlert("HTTP Error", "Failed to load data. Status code: " + conn.getResponseCode(), Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Exception", "Error loading attendance logs:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onSearch() {
        String code = searchEmployeeCode.getText().trim();
        LocalDate date = searchDate.getValue();

        ObservableList<AttendanceLogFx> filtered = allLogs.filtered(log -> {
            boolean matchCode = (code.isEmpty() || (log.getEmployeeCode() != null && log.getEmployeeCode().equalsIgnoreCase(code)));
            boolean matchDate = (date == null || (log.getAttendanceDate() != null && log.getAttendanceDate().isEqual(date)));
            return matchCode && matchDate;
        });

        attendanceTable.setItems(filtered);
    }

    @FXML
    private void onReset() {
        searchEmployeeCode.clear();
        searchDate.setValue(null);
        attendanceTable.setItems(allLogs);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void openEditWindow(AttendanceLogFx selectedLog) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_attendance.fxml"));
            Parent root = loader.load();

            EditAttendanceController controller = loader.getController();
            controller.setAttendanceLog(selectedLog);

            Stage stage = new Stage();
            stage.setTitle("Edit Attendance");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // Wait until the window is closed

            // Reload table after editing
            loadAttendanceLogs();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open edit window:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
