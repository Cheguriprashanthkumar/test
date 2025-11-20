package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.EmployeeFx;
import org.nishgrid.clienterp.service.ApiService;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditEmployeeController {

    private EmployeeFx employee;
    // Callback to notify the main controller to refresh the table
    private Runnable saveCallback;

    @FXML private TextField employeeCodeField, fullNameField, genderField, departmentField,
            designationField, mobileNumber1Field, mobileNumber2Field, emailField,
            aadhaarNumberField, panCardNumberField, addressField, statusField, photoField, fingerPrintField;

    @FXML private DatePicker dobPicker, dojPicker;

    public void setEmployee(EmployeeFx emp) {
        this.employee = emp;

        employeeCodeField.setText(emp.getEmployeeCode());
        fullNameField.setText(emp.getFullName());
        genderField.setText(emp.getGender());
        dobPicker.setValue(emp.getDob());
        dojPicker.setValue(emp.getDoj());
        departmentField.setText(emp.getDepartment());
        designationField.setText(emp.getDesignation());
        mobileNumber1Field.setText(String.valueOf(emp.getMobileNumber1()));
        mobileNumber2Field.setText(String.valueOf(emp.getMobileNumber2()));
        emailField.setText(emp.getEmail());
        aadhaarNumberField.setText(String.valueOf(emp.getAadhaarNumber()));
        panCardNumberField.setText(emp.getPanCardNumber());
        addressField.setText(emp.getAddress());
        statusField.setText(emp.getStatus());
        photoField.setText(emp.getPhoto());
        fingerPrintField.setText(emp.getFingerPrint());
    }

    // Setter for the callback
    public void setSaveCallback(Runnable saveCallback) {
        this.saveCallback = saveCallback;
    }

    @FXML
    public void onSave() {
        try {
            // Update employee object from form fields
            employee.setEmployeeCode(employeeCodeField.getText());
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

            // Setup HTTP PUT request
            URL url = new URL(ApiService.getBaseUrl()+"/employees/" + employee.getEmployeeId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Write JSON data to the connection
            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, employee);
            }

            // Check response and show feedback
            if (conn.getResponseCode() == 200) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully");
                // Trigger the callback to refresh the main table
                if (saveCallback != null) {
                    saveCallback.run();
                }
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update employee. Code: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid input or network issue: " + e.getMessage());
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

    private void showAlert(Alert.AlertType alertType, String title, String msg) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) employeeCodeField.getScene().getWindow();
        stage.close();
    }
}
