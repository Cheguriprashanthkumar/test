package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.EmployeeFx;
import org.nishgrid.clienterp.service.ApiService;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class EmployeeDetailsController {

    private EmployeeFx employee;
    private Runnable callback;

    @FXML private ImageView employeePhoto;
    @FXML private TextField fullNameField, genderField, departmentField,
            designationField, mobileNumber1Field, mobileNumber2Field, emailField,
            aadhaarNumberField, panCardNumberField, addressField, statusField, photoField, fingerPrintField;
    @FXML private DatePicker dobPicker, dojPicker;
    @FXML private Button editButton, saveButton, deleteButton;
    @FXML private GridPane detailsGrid;

    public void setEmployee(EmployeeFx emp, boolean isEditable) {
        this.employee = emp;
        populateForm();
        setFormEditable(isEditable);
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    private void populateForm() {
        if (employee == null) return;

        fullNameField.setText(employee.getFullName());
        genderField.setText(employee.getGender());
        dobPicker.setValue(employee.getDob());
        dojPicker.setValue(employee.getDoj());
        departmentField.setText(employee.getDepartment());
        designationField.setText(employee.getDesignation());
        mobileNumber1Field.setText(String.valueOf(employee.getMobileNumber1()));
        mobileNumber2Field.setText(String.valueOf(employee.getMobileNumber2()));
        emailField.setText(employee.getEmail());
        aadhaarNumberField.setText(String.valueOf(employee.getAadhaarNumber()));
        panCardNumberField.setText(employee.getPanCardNumber());
        addressField.setText(employee.getAddress());
        statusField.setText(employee.getStatus());
        photoField.setText(employee.getPhoto());
        fingerPrintField.setText(employee.getFingerPrint());

        try {
            Image image = new Image(employee.getPhoto(), true);
            employeePhoto.setImage(image);
        } catch (Exception e) {
            employeePhoto.setImage(new Image(getClass().getResourceAsStream("/icons/placeholder.jpeg")));
        }
    }

    private void setFormEditable(boolean isEditable) {
        // Loop through all TextFields and DatePickers in the grid and set their editable state
        for (Node node : detailsGrid.getChildren()) {
            if (node instanceof TextInputControl) {
                ((TextInputControl) node).setEditable(isEditable);
            } else if (node instanceof DatePicker) {
                ((DatePicker) node).setEditable(isEditable);
                // Make date pickers non-interactive visually when not editable
                node.setMouseTransparent(!isEditable);
                node.setFocusTraversable(isEditable);
            }
        }
        // Also set the top-level name and email fields
        fullNameField.setEditable(isEditable);
        emailField.setEditable(isEditable);

        // Toggle button visibility based on mode
        editButton.setVisible(!isEditable);
        saveButton.setVisible(isEditable);
        deleteButton.setVisible(isEditable);
    }

    @FXML
    private void onEnableEdit() {
        setFormEditable(true);
        Stage stage = (Stage) editButton.getScene().getWindow();
        if (stage != null) {
            stage.setTitle("Edit Employee Details");
        }
    }

    @FXML
    private void onSave() {
        try {
            employee.setFullName(fullNameField.getText());
            employee.setGender(genderField.getText());
            employee.setDob(dobPicker.getValue());
            employee.setDoj(dojPicker.getValue());
            employee.setDepartment(departmentField.getText());
            employee.setDesignation(designationField.getText());
            employee.setMobileNumber1(parseLongSafe(mobileNumber1Field.getText()));
            employee.setMobileNumber2(parseLongSafe(mobileNumber2Field.getText()));
            employee.setAadhaarNumber(parseLongSafe(aadhaarNumberField.getText()));
            employee.setEmail(emailField.getText());
            employee.setPanCardNumber(panCardNumberField.getText());
            employee.setAddress(addressField.getText());
            employee.setStatus(statusField.getText());
            employee.setPhoto(photoField.getText());
            employee.setFingerPrint(fingerPrintField.getText());

            URL url = new URL(ApiService.getBaseUrl()+"/employees/" + employee.getEmployeeId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, employee);
            }

            if (conn.getResponseCode() == 200) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully.");
                if (callback != null) callback.run();
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update employee. Code: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid input or network issue: " + e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Employee: " + employee.getFullName());
        confirmation.setContentText("Are you sure you want to delete this data? This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                URL url = new URL(ApiService.getBaseUrl()+"/employees/" + employee.getEmployeeId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                if (conn.getResponseCode() == 200 || conn.getResponseCode() == 204) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully.");
                    if (callback != null) callback.run();
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete employee.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Network Error", "Could not connect to server.");
            }
        }
    }

    private Long parseLongSafe(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
