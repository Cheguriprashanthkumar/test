package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.EmployeeFx;
import org.nishgrid.clienterp.service.ApiService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EmployeeTabController {

    @FXML private TableView<EmployeeFx> employeeTable;
    @FXML private TextField searchField;

    private FilteredList<EmployeeFx> filteredList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadEmployeesFromBackend();
        setupSearch();
    }

    private void setupTableColumns() {
        TableColumn<EmployeeFx, String> codeCol = new TableColumn<>("Employee Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));

        TableColumn<EmployeeFx, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<EmployeeFx, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<EmployeeFx, String> desigCol = new TableColumn<>("Designation");
        desigCol.setCellValueFactory(new PropertyValueFactory<>("designation"));

        TableColumn<EmployeeFx, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("View/Edit");

            {
                editBtn.setOnAction(e -> {
                    EmployeeFx emp = getTableView().getItems().get(getIndex());
                    openEditDialog(emp);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });

        employeeTable.getColumns().setAll(codeCol, nameCol, deptCol, desigCol, actionCol);
    }

    private void loadEmployeesFromBackend() {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/employees");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    json.append(line);
                }
                in.close();

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                List<EmployeeFx> employees = mapper.readValue(json.toString(), new TypeReference<List<EmployeeFx>>() {});
                filteredList = new FilteredList<>(FXCollections.observableArrayList(employees), p -> true);
                employeeTable.setItems(filteredList);
            } else {
                showAlert("Error", "Failed to fetch employees. HTTP code: " + conn.getResponseCode(), Alert.AlertType.ERROR);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load employees: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(emp -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return emp.getEmployeeCode().toLowerCase().contains(newVal.toLowerCase())
                        || emp.getFullName().toLowerCase().contains(newVal.toLowerCase());
            });
        });
    }

    private void openEditDialog(EmployeeFx emp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_employee.fxml"));
            Parent root = loader.load();

            EditEmployeeController controller = loader.getController();
            controller.setEmployee(emp);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Employee");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadEmployeesFromBackend(); // refresh after edit
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open edit dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
