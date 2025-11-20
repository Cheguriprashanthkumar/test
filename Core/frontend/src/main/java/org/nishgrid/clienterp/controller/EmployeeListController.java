package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.EmployeeFx;
import org.nishgrid.clienterp.service.ApiService;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeListController implements Initializable {

    @FXML private TableView<EmployeeFx> employeeTable;
    @FXML private TableColumn<EmployeeFx, String> colEmployeeCode;
    @FXML private TableColumn<EmployeeFx, String> colFullName;
    @FXML private TableColumn<EmployeeFx, String> colDepartment;
    @FXML private TableColumn<EmployeeFx, String> colDesignation;
    @FXML private TableColumn<EmployeeFx, Void> colActions;
    @FXML private TextField searchField;

    private final ObservableList<EmployeeFx> employeeList = FXCollections.observableArrayList();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapper.registerModule(new JavaTimeModule());
        setupTableColumns();
        loadEmployeeData();
        setupSearchFilter();
    }

    private void setupTableColumns() {
        colEmployeeCode.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));
        addActionsToTable();
    }

    private void loadEmployeeData() {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/employees");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch employees. Code: " + conn.getResponseCode());
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            List<EmployeeFx> employees = mapper.readValue(br, new TypeReference<List<EmployeeFx>>() {});
            employeeList.setAll(employees);
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Network Error", "Could not connect to the server.");
        }
    }

    private void setupSearchFilter() {
        FilteredList<EmployeeFx> filteredData = new FilteredList<>(employeeList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (employee.getEmployeeCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (employee.getFullName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<EmployeeFx> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(employeeTable.comparatorProperty());
        employeeTable.setItems(sortedData);
    }

    private void addActionsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final ImageView viewIcon = createIcon("/icons/view.png");
            private final Button viewButton = new Button("View Details", viewIcon);
            private final HBox pane = new HBox(viewButton);

            {
                viewButton.setOnAction(event -> {
                    EmployeeFx employee = getTableView().getItems().get(getIndex());
                    openDetailsWindow(employee);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void openDetailsWindow(EmployeeFx employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employee-details.fxml"));
            Parent root = loader.load();

            EmployeeDetailsController controller = loader.getController();
            controller.setEmployee(employee, false);
            controller.setCallback(this::refreshTable);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("View Employee Details");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open the details window.");
        }
    }

    private void refreshTable() {
        loadEmployeeData();
    }

    /**
     * FIX: This method now checks if the icon file exists before trying to load it.
     * This prevents the NullPointerException.
     */
    private ImageView createIcon(String path) {
        ImageView imageView = new ImageView();
        try {
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                Image image = new Image(stream);
                imageView.setImage(image);
                imageView.setFitHeight(16);
                imageView.setFitWidth(16);
            } else {
                System.err.println("Cannot load icon: " + path + " (file not found in resources)");
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path);
            e.printStackTrace();
        }
        return imageView;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
