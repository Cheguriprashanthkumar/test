package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nishgrid.clienterp.service.ApiService;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IdCardController {

    // --- FXML UI Components for ID Card ---
    @FXML private TextField employeeCodeField;
    @FXML private VBox idCardBox;
    @FXML private Label nameLabel, designationLabel, departmentLabel, emailLabel, mobileLabel, dojLabel;
    @FXML private ImageView photoView;

    // --- FXML UI Components for Employee Table ---
    @FXML private TableView<EmployeeInfo> employeeTableView;
    @FXML private TableColumn<EmployeeInfo, String> codeColumn;
    @FXML private TableColumn<EmployeeInfo, String> nameColumn;
    @FXML private TableColumn<EmployeeInfo, String> designationColumn;
    @FXML private TextField searchField; // NEW: Injection for the search field

    // --- Data Holders ---
    private JSONObject currentEmployeeData;
    private final ObservableList<EmployeeInfo> employeeData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Set up the table columns
        setupTableColumns();

        // 2. Set up the filtering logic
        setupFilterableTable();

        // 3. Load the employee list from the backend
        loadAllEmployees();
    }

    private void setupTableColumns() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        designationColumn.setCellValueFactory(new PropertyValueFactory<>("designation"));

        employeeTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        employeeCodeField.setText(newSelection.getEmployeeCode());
                        onGenerateClick();
                    }
                });
    }

    /**
     * Sets up the search functionality for the employee table.
     */
    private void setupFilterableTable() {
        // 1. Wrap the base data list in a FilteredList.
        FilteredList<EmployeeInfo> filteredData = new FilteredList<>(employeeData, p -> true);

        // 2. Add a listener to the search field's text property.
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {
                // If filter text is empty, display all employees.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Check if employee details contain the filter text.
                if (employee.getFullName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches full name.
                } else if (employee.getEmployeeCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches employee code.
                } else if (employee.getDesignation().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches designation.
                }

                return false; // Does not match.
            });
        });

        // 3. Bind the TableView to the FilteredList.
        employeeTableView.setItems(filteredData);
    }

    private void loadAllEmployees() {
        // (This method is identical to the previous version, no changes here)
        new Thread(() -> {
            try {
                URL url = new URL(ApiService.getBaseUrl()+"/employees");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Accept", "application/json");

                if (con.getResponseCode() != 200) {
                    Platform.runLater(() -> showAlert("Failed to load employees. Server error."));
                    return;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
                in.close();

                JSONArray employeesJson = new JSONArray(responseBuilder.toString());
                final List<EmployeeInfo> employeeList = new ArrayList<>();
                for (int i = 0; i < employeesJson.length(); i++) {
                    JSONObject emp = employeesJson.getJSONObject(i);
                    employeeList.add(new EmployeeInfo(
                            emp.optString("employeeCode", "N/A"),
                            emp.optString("fullName", "N/A"),
                            emp.optString("designation", "N/A")
                    ));
                }

                Platform.runLater(() -> {
                    employeeData.setAll(employeeList);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Error connecting to server to load employee list."));
            }
        }).start();
    }

    // --- onGenerateClick(), onDownloadClick(), showAlert(), and EmployeeInfo class are unchanged ---

    @FXML
    public void onGenerateClick() {
        String empCode = employeeCodeField.getText().trim();
        if (empCode.isEmpty()) {
            showAlert("Please enter an employee code or select one from the table.");
            return;
        }

        try {
            URL url = new URL(ApiService.getBaseUrl()+"/employees/idcard/" + empCode);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            if (con.getResponseCode() != 200) {
                showAlert("Employee not found or server error.");
                idCardBox.setVisible(false);
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = in.readLine();
            in.close();

            JSONObject emp = new JSONObject(response);
            currentEmployeeData = emp;

            nameLabel.setText("Name: " + emp.optString("fullName", "N/A"));
            designationLabel.setText("Designation: " + emp.optString("designation", "N/A"));
            departmentLabel.setText("Department: " + emp.optString("department", "N/A"));
            emailLabel.setText("Email: " + emp.optString("email", "N/A"));
            mobileLabel.setText("Mobile: " + emp.optString("mobileNumber", "N/A"));
            dojLabel.setText("DOJ: " + emp.optString("doj", "N/A"));

            String photoPath = emp.optString("photo", "");
            if (!photoPath.isEmpty()) {
                File file = new File(photoPath);
                photoView.setImage(file.exists() ? new Image(file.toURI().toString()) : null);
            } else {
                photoView.setImage(null);
            }

            idCardBox.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error fetching employee data.");
        }
    }

    @FXML
    public void onDownloadClick() {
        if (!idCardBox.isVisible() || currentEmployeeData == null) {
            showAlert("Please generate an ID card first before downloading.");
            return;
        }

        try {
            WritableImage snapshot = idCardBox.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            String empCode = currentEmployeeData.optString("employeeCode", "UNKNOWN");
            String homeDir = System.getProperty("user.home");
            File downloadsDir = new File(homeDir, "Downloads");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdir();
            }

            String fileName = "Employee_ID_Card_" + empCode + ".png";
            File output = new File(downloadsDir, fileName);

            ImageIO.write(bufferedImage, "png", output);
            showAlert("ID Card saved successfully to: " + output.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to save ID card.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class EmployeeInfo {
        private final SimpleStringProperty employeeCode;
        private final SimpleStringProperty fullName;
        private final SimpleStringProperty designation;

        public EmployeeInfo(String employeeCode, String fullName, String designation) {
            this.employeeCode = new SimpleStringProperty(employeeCode);
            this.fullName = new SimpleStringProperty(fullName);
            this.designation = new SimpleStringProperty(designation);
        }

        public String getEmployeeCode() { return employeeCode.get(); }
        public String getFullName() { return fullName.get(); }
        public String getDesignation() { return designation.get(); }
    }
}