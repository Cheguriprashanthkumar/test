package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import org.nishgrid.clienterp.model.EmployeeExit;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import org.nishgrid.clienterp.service.ApiService;
public class ExitTableController {

    @FXML private TableView<EmployeeExit> exitTableView;
    @FXML private TableColumn<EmployeeExit, Integer> colId;
    @FXML private TableColumn<EmployeeExit, String> colCode;
    @FXML private TableColumn<EmployeeExit, LocalDate> colDate;
    @FXML private TableColumn<EmployeeExit, String> colReason;
    @FXML private TableColumn<EmployeeExit, String> colSettlement;
    @FXML private TableColumn<EmployeeExit, String> colFeedback;
    @FXML private TableColumn<EmployeeExit, String> colStatus;


    private final ObjectMapper objectMapper;

    private final ObservableList<EmployeeExit> exitList = FXCollections.observableArrayList();
    private void fetchExitData() {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/exits");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }

            List<EmployeeExit> exits = objectMapper.readValue(json.toString(), new TypeReference<List<EmployeeExit>>() {});

            // 🔧 Manually populate employeeCode from embedded employee
            for (EmployeeExit exit : exits) {
                if (exit.getEmployee() != null && exit.getEmployee().getEmployeeCode() != null) {
                    exit.setEmployeeCode(exit.getEmployee().getEmployeeCode());
                }
            }

            exitList.setAll(exits);
            exitTableView.setItems(exitList);

            scanner.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colCode.setCellValueFactory(data -> data.getValue().employeeCodeProperty());
        colDate.setCellValueFactory(data -> data.getValue().exitDateProperty());
        colReason.setCellValueFactory(data -> data.getValue().reasonProperty());
        colSettlement.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getFinalSettlement()))
        );


        colFeedback.setCellValueFactory(data -> data.getValue().feedbackProperty());
        colStatus.setCellValueFactory(data -> data.getValue().clearanceStatusProperty());

        fetchExitData();
    }

    @FXML
    public void onAddExit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_exit.fxml"));
            Parent root = loader.load();

            EditExitController controller = loader.getController();
            controller.setEditMode(false, null);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Exit");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            fetchExitData(); // Refresh table
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditSelected() {
        EmployeeExit selected = exitTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a record to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_exit.fxml"));
            Parent root = loader.load();

            EditExitController controller = loader.getController();
            controller.setEditMode(true, selected);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Exit");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            fetchExitData(); // Refresh table
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDeleteSelected() {
        EmployeeExit selected = exitTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a record to delete.");
            return;
        }

        try {
            URL url = new URL(ApiService.getBaseUrl()+"/exit/" + selected.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                fetchExitData();
                showAlert("Exit record deleted successfully.");
            } else {
                showAlert("Failed to delete the exit record.");
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error occurred while deleting the record.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML private TextField searchField;

    @FXML
    public void onSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            exitTableView.setItems(exitList);
            return;
        }

        ObservableList<EmployeeExit> filtered = FXCollections.observableArrayList();
        for (EmployeeExit exit : exitList) {
            if (exit.getEmployeeCode().toLowerCase().contains(searchTerm)) {
                filtered.add(exit);
            }
        }

        exitTableView.setItems(filtered);
    }


    public ExitTableController() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}
