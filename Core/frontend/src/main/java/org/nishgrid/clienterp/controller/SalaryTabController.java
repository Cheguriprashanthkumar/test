package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.Salary;
import org.nishgrid.clienterp.service.ApiService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SalaryTabController {

    @FXML private TableView<Salary> salaryTable;
    @FXML private TextField searchField;
    @FXML private TableColumn<Salary, String> employeeCodeCol;
    @FXML private TableColumn<Salary, Double> basicCol, hraCol, otherCol, pfCol, esiCol, tdsCol, totalCol, yearCol;
    @FXML private TableColumn<Salary, Void> actionCol;

    private final ObservableList<Salary> salaryData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        employeeCodeCol.setCellValueFactory(cell -> {
            if (cell.getValue().getEmployee() != null) {
                return new SimpleStringProperty(cell.getValue().getEmployee().getEmployeeCode());
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        basicCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getBasicSalary()).asObject());
        hraCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getHra()).asObject());
        otherCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getOtherAllowances()).asObject());
        pfCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPfDeduction()).asObject());
        esiCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getEsiDeduction()).asObject());
        tdsCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTdsDeduction()).asObject());
        totalCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotalSalary()).asObject());
        yearCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getYearSalary()).asObject());

        addEditButtonToTable();
        fetchSalaryData();
        setupSearchFilter();
    }

    private void fetchSalaryData() {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/salary");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            List<Salary> list = mapper.readValue(in, new TypeReference<List<Salary>>() {});
            salaryData.setAll(list);
            salaryTable.setItems(salaryData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearchFilter() {
        FilteredList<Salary> filtered = new FilteredList<>(salaryData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filtered.setPredicate(salary -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String query = newVal.toLowerCase();
                return salary.getEmployee() != null &&
                        salary.getEmployee().getEmployeeCode().toLowerCase().contains(query);
            });
        });

        salaryTable.setItems(filtered);
    }

    private void addEditButtonToTable() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");

            {
                editBtn.setOnAction(event -> {
                    Salary selected = getTableView().getItems().get(getIndex());
                    openEditWindow(selected);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });
    }

    private void openEditWindow(Salary salary) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_salary.fxml"));
            AnchorPane pane = loader.load();

            EditSalaryController controller = loader.getController();
            controller.setSalary(salary); // ✅ Pass Salary directly

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Salary");
            stage.setScene(new Scene(pane));
            stage.showAndWait();

            salaryTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
