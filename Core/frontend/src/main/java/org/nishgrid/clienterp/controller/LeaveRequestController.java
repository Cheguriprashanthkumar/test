package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.LeaveRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import org.nishgrid.clienterp.service.ApiService;
public class LeaveRequestController {

    @FXML private TableView<LeaveRequest> leaveTable;
    @FXML private TableColumn<LeaveRequest, Integer> colLeaveId;
    @FXML private TableColumn<LeaveRequest, String> colEmployeeCode;
    @FXML private TableColumn<LeaveRequest, String> colFullName;
    @FXML private TableColumn<LeaveRequest, String> colLeaveType;
    @FXML private TableColumn<LeaveRequest, String> colFromDate;
    @FXML private TableColumn<LeaveRequest, String> colToDate;
    @FXML private TableColumn<LeaveRequest, Integer> colTotalDays;
    @FXML private TableColumn<LeaveRequest, String> colStatus;
    @FXML private TableColumn<LeaveRequest, String> colApprovedBy;
    @FXML private TableColumn<LeaveRequest, String> colLeavePaymentType;
    @FXML private TableColumn<LeaveRequest, Void> colAction;

    @FXML private TextField txtSearch;
    @FXML private Button btnSearch, btnRefresh;

    private final ObservableList<LeaveRequest> data = FXCollections.observableArrayList();

    public void initialize() {
        setupTableColumns();
        loadAllLeaveRequests();

        btnSearch.setOnAction(e -> searchLeaveByEmployeeCode(txtSearch.getText().trim()));
        btnRefresh.setOnAction(e -> loadAllLeaveRequests());
    }

    private void setupTableColumns() {
        colLeaveId.setCellValueFactory(cell -> cell.getValue().leaveIdProperty().asObject());
        colLeaveType.setCellValueFactory(cell -> cell.getValue().leaveTypeProperty());
        colFromDate.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFromDate() != null ? cell.getValue().getFromDate().toString() : "")
        );
        colToDate.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getToDate() != null ? cell.getValue().getToDate().toString() : "")
        );
        colTotalDays.setCellValueFactory(cell -> cell.getValue().totalDaysProperty().asObject());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colApprovedBy.setCellValueFactory(cell -> cell.getValue().approvedByProperty());
        colLeavePaymentType.setCellValueFactory(cell -> cell.getValue().leavePaymentTypeProperty());

        // ✅ Use SimpleStringProperty for nested employee fields
        colEmployeeCode.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmployeeCode()));
        colFullName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFullName()));

        // Action buttons (edit)
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");

            {
                editBtn.setOnAction(event -> {
                    LeaveRequest lr = getTableView().getItems().get(getIndex());
                    openEditWindow(lr);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editBtn));
                }
            }
        });
    }

    private void loadAllLeaveRequests() {
        data.clear();
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/leave-requests");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (sc.hasNext()) json.append(sc.nextLine());
            sc.close();

            // ✅ Register JavaTimeModule to handle LocalDate
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            List<LeaveRequest> leaves = mapper.readValue(json.toString(), new TypeReference<>() {});
            data.addAll(leaves);
            leaveTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load leave requests");
        }
    }


    private void searchLeaveByEmployeeCode(String code) {
        if (code.isEmpty()) {
            loadAllLeaveRequests();
            return;
        }
        data.clear();
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/leave-requests/employee/" + code);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (sc.hasNext()) json.append(sc.nextLine());
            sc.close();

            ObjectMapper mapper = new ObjectMapper();
            List<LeaveRequest> leaves = mapper.readValue(json.toString(), new TypeReference<>() {});
            data.addAll(leaves);
            leaveTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Search failed for employee: " + code);
        }
    }

    private void openEditWindow(LeaveRequest leaveRequest) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_leave.fxml"));
            VBox root = loader.load();

            EditLeaveController controller = loader.getController();
            controller.setLeaveRequest(leaveRequest);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Leave Request");
            stage.showAndWait();

            loadAllLeaveRequests(); // refresh after editing
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to open edit window");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
