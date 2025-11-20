package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.EmployeeRegistrationRequestFx;
import org.nishgrid.clienterp.service.ApiService;
import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EmployeeRegistrationController {

    // --- FXML UI Component Injections ---
    @FXML private TextField fullNameField;
    @FXML private ChoiceBox<String> genderChoice;
    @FXML private DatePicker dobPicker;
    @FXML private DatePicker dojPicker;
    @FXML private TextField departmentField;
    @FXML private TextField designationField;
    @FXML private TextField mobile1Field;
    @FXML private TextField mobile2Field;
    @FXML private TextField emailField;
    @FXML private TextField aadhaarNumberField;
    @FXML private TextField panNumberField;
    @FXML private TextField aadhaarDocField;
    @FXML private TextField panDocField;
    @FXML private TextArea addressArea;
    @FXML private ChoiceBox<String> statusChoice;
    @FXML private TextField photoField;
    @FXML private TextField fingerprintField;
    @FXML private TextField basicSalaryField;
    @FXML private TextField hraField;
    @FXML private TextField otherAllowancesField;
    @FXML private TextField pfDeductionField;
    @FXML private TextField esiDeductionField;
    @FXML private TextField tdsDeductionField;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    public void initialize() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Helper method to get the current window (Stage) for this controller.
     * It finds the window by starting from any control within it.
     */
    private Stage getStage() {
        return (Stage) fullNameField.getScene().getWindow();
    }

    /**
     * This is the updated method for the "Cancel" button.
     * It correctly closes ONLY the current window (the "module").
     * It will not exit the entire application unless this is the last open window.
     */
    @FXML
    private void handleCancel() {
        getStage().close();
    }

    private String browseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("PDF Documents", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(getStage());
        return selectedFile != null ? selectedFile.getAbsolutePath() : "";
    }

    @FXML private void handleBrowseAadhaar() { aadhaarDocField.setText(browseFile()); }
    @FXML private void handleBrowsePan() { panDocField.setText(browseFile()); }
    @FXML private void handleBrowsePhoto() { photoField.setText(browseFile()); }
    @FXML private void handleBrowseFingerprint() { fingerprintField.setText(browseFile()); }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        EmployeeRegistrationRequestFx payload = createPayloadFromForm();

        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(ApiService.getBaseUrl()+"/employees/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(this::handleServerResponse)
                    .exceptionally(this::handleConnectionError);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Application Error", "An error occurred: " + e.getMessage());
        }
    }

    private void handleServerResponse(HttpResponse<String> response) {
        Platform.runLater(() -> {
            if (response.statusCode() == 200) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee registered successfully!");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to register employee: " + response.body());
            }
        });
    }

    private Void handleConnectionError(Throwable ex) {
        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the server: " + ex.getMessage()));
        return null;
    }

    private EmployeeRegistrationRequestFx createPayloadFromForm() {
        EmployeeRegistrationRequestFx payload = new EmployeeRegistrationRequestFx();
        payload.setFullName(fullNameField.getText());
        payload.setGender(genderChoice.getValue());
        payload.setDob(dobPicker.getValue());
        payload.setDoj(dojPicker.getValue());
        payload.setDepartment(departmentField.getText());
        payload.setDesignation(designationField.getText());
        payload.setMobileNumber1(mobile1Field.getText());
        payload.setMobileNumber2(mobile2Field.getText());
        payload.setEmail(emailField.getText());
        payload.setAadhaarNumber(aadhaarNumberField.getText());
        payload.setPanCardNumber(panNumberField.getText());
        payload.setAadhaarCardDocument(aadhaarDocField.getText());
        payload.setPanCardDocument(panDocField.getText());
        payload.setAddress(addressArea.getText());
        payload.setStatus(statusChoice.getValue());
        payload.setPhoto(photoField.getText());
        payload.setFingerPrint(fingerprintField.getText());
        payload.setBasicSalary(parseBigDecimalSafe(basicSalaryField.getText()));
        payload.setHra(parseBigDecimalSafe(hraField.getText()));
        payload.setOtherAllowances(parseBigDecimalSafe(otherAllowancesField.getText()));
        payload.setPfDeduction(parseBigDecimalSafe(pfDeductionField.getText()));
        payload.setEsiDeduction(parseBigDecimalSafe(esiDeductionField.getText()));
        payload.setTdsDeduction(parseBigDecimalSafe(tdsDeductionField.getText()));
        return payload;
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        if (fullNameField.getText().isBlank()) errors.append("Full Name is required.\n");
        if (genderChoice.getValue() == null) errors.append("Gender is required.\n");
        if (dobPicker.getValue() == null) errors.append("Date of Birth is required.\n");
        if (dojPicker.getValue() == null) errors.append("Date of Joining is required.\n");
        if (departmentField.getText().isBlank()) errors.append("Department is required.\n");
        if (designationField.getText().isBlank()) errors.append("Designation is required.\n");
        if (statusChoice.getValue() == null) errors.append("Status is required.\n");
        if (emailField.getText().isBlank()) {
            errors.append("Email is required.\n");
        } else if (!emailField.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.append("Email format is invalid.\n");
        }
        if (mobile1Field.getText().isBlank()) {
            errors.append("Primary Mobile Number is required.\n");
        } else if (!mobile1Field.getText().matches("\\d{10}")) {
            errors.append("Primary Mobile Number must be exactly 10 digits.\n");
        }
        if (!mobile2Field.getText().isBlank() && !mobile2Field.getText().matches("\\d{10}")) {
            errors.append("Secondary Mobile Number must be 10 digits if provided.\n");
        }
        if (!aadhaarNumberField.getText().isBlank() && !aadhaarNumberField.getText().matches("\\d{12}")) {
            errors.append("Aadhaar Number must be 12 digits if provided.\n");
        }
        if (!panNumberField.getText().isBlank() && !panNumberField.getText().matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
            errors.append("PAN Card Number format is invalid.\n");
        }
        if (basicSalaryField.getText().isBlank()) errors.append("Basic Salary is required.\n");
        validateNumericField(basicSalaryField, "Basic Salary", errors);
        validateNumericField(hraField, "HRA", errors);
        validateNumericField(otherAllowancesField, "Other Allowances", errors);
        validateNumericField(pfDeductionField, "PF Deduction", errors);
        validateNumericField(esiDeductionField, "ESI Deduction", errors);
        validateNumericField(tdsDeductionField, "TDS Deduction", errors);
        if (!errors.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Errors", "Please fix the following issues:\n\n" + errors);
            return false;
        }
        return true;
    }

    private void validateNumericField(TextField field, String fieldName, StringBuilder errors) {
        if (!field.getText().isBlank() && !field.getText().matches("^\\d*\\.?\\d+$")) {
            errors.append(fieldName).append(" must be a valid positive number.\n");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().setPrefSize(480, 200);
        alert.setResizable(true);
        alert.showAndWait();
    }
// Add this new method to your EmployeeRegistrationController.java file

    @FXML
    private void handleReset() {
        // This method is called by the "Reset" button in your FXML.
        // It calls your existing logic to clear all the fields.
        clearForm();
    }
    private BigDecimal parseBigDecimalSafe(String text) {
        return (text == null || text.isBlank()) ? BigDecimal.ZERO : new BigDecimal(text);
    }

    private void clearForm() {
        fullNameField.clear();
        genderChoice.getSelectionModel().clearSelection();
        dobPicker.setValue(null);
        dojPicker.setValue(null);
        departmentField.clear();
        designationField.clear();
        mobile1Field.clear();
        mobile2Field.clear();
        emailField.clear();
        aadhaarNumberField.clear();
        panNumberField.clear();
        aadhaarDocField.clear();
        panDocField.clear();
        addressArea.clear();
        statusChoice.getSelectionModel().clearSelection();
        photoField.clear();
        fingerprintField.clear();
        basicSalaryField.clear();
        hraField.clear();
        otherAllowancesField.clear();
        pfDeductionField.clear();
        esiDeductionField.clear();
        tdsDeductionField.clear();
    }
}