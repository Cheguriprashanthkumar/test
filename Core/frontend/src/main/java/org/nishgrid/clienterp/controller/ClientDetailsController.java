package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.nishgrid.clienterp.model.LicenseResponse;
import org.nishgrid.clienterp.util.ConfigManager;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ClientDetailsController {

    @FXML
    private TextField idField, fullNameField, companyNameField, emailField, companyTypeField, mobileField;
    @FXML
    private TextField gstField, pincodeField, cityField, licenseKeyField, startDateField, endDateField;
    @FXML
    private TextArea addressArea;
    @FXML
    private ComboBox<Map<String, Object>> countryComboBox, stateComboBox;
    @FXML
    private PasswordField adminPasswordField;
    @FXML
    private Label logoPathLabel;
    @FXML
    private ImageView logoPreview;

    private String logoFilePath = "";
    private File selectedLogoFile;
    private LicenseResponse licenseResponse;
    private ObjectMapper objectMapper = createMapper();

    @FXML
    public void initialize() {
        loadCountries();
        idField.setDisable(false);
        idField.setEditable(false);

        countryComboBox.setOnAction(e -> {
            Map<String, Object> selectedCountry = countryComboBox.getValue();
            if (selectedCountry != null) {
                Integer geonameId = (Integer) selectedCountry.get("geonameId");
                loadStates(geonameId);
            }
        });

        pincodeField.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (!newFocus) handleFetchGeoLocation();
        });

        Platform.runLater(() -> {
            ConfigManager configManager = ConfigManager.getInstance();
            LicenseResponse savedLicense = configManager.getSavedLicenseDetails();
            if (savedLicense != null && savedLicense.getLicenseKey() != null && !savedLicense.getLicenseKey().isBlank()) {
                setLicenseResponse(savedLicense);
            }
        });
    }

    public void setLicenseResponse(LicenseResponse response) {
        this.licenseResponse = response;
        if (response != null) {
            Platform.runLater(() -> prefillLicenseFields(response));
        }
    }

    private void prefillLicenseFields(LicenseResponse license) {
        idField.setText(license.getUniqueId());
        licenseKeyField.setText(license.getLicenseKey());
        fullNameField.setText(license.getFullName());
        companyNameField.setText(license.getCompanyName());
        emailField.setText(license.getEmailAddress());
        startDateField.setText(String.valueOf(license.getStartDate()));
        endDateField.setText(String.valueOf(license.getEndDate()));

        licenseKeyField.setEditable(false);
        fullNameField.setEditable(false);
        companyNameField.setEditable(false);
        emailField.setEditable(false);
        startDateField.setEditable(false);
        endDateField.setEditable(false);
    }

    private void loadCountries() {
        String url = ApiService.getBaseUrl() + "/geonames/countries";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> json = mapper.readValue(responseBody, Map.class);
                        List<Map<String, Object>> countries = (List<Map<String, Object>>) json.get("geonames");
                        ObservableList<Map<String, Object>> countryList = FXCollections.observableArrayList(countries);

                        Platform.runLater(() -> {
                            countryComboBox.setItems(countryList);
                            countryComboBox.setConverter(new javafx.util.StringConverter<>() {
                                public String toString(Map<String, Object> object) {
                                    return object == null ? "" : (String) object.get("countryName");
                                }
                                public Map<String, Object> fromString(String string) { return null; }
                            });

                            countries.stream()
                                    .filter(c -> "IN".equals(c.get("countryCode")))
                                    .findFirst()
                                    .ifPresent(countryComboBox.getSelectionModel()::select);
                        });
                    } catch (Exception ignored) {}
                });
    }

    private void loadStates(int countryGeonameId) {
        String url = String.format(ApiService.getBaseUrl() + "/geonames/states?countryGeonameId=%d", countryGeonameId);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> json = mapper.readValue(responseBody, Map.class);
                        List<Map<String, Object>> states = (List<Map<String, Object>>) json.get("geonames");
                        ObservableList<Map<String, Object>> stateList = FXCollections.observableArrayList(states);

                        Platform.runLater(() -> {
                            stateComboBox.setItems(stateList);
                            stateComboBox.setConverter(new javafx.util.StringConverter<>() {
                                public String toString(Map<String, Object> object) {
                                    return object == null ? "" : (String) object.get("name");
                                }
                                public Map<String, Object> fromString(String string) { return null; }
                            });
                        });
                    } catch (Exception ignored) {}
                });
    }

    @FXML
    private void handleFetchGeoLocation() {
        String pincode = pincodeField.getText().trim();
        Map<String, Object> selectedCountry = countryComboBox.getValue();
        if (pincode.isEmpty() || selectedCountry == null) return;
        String countryCode = (String) selectedCountry.get("countryCode");
        String url = String.format(ApiService.getBaseUrl() + "/geonames/postalcode?postalCode=%s&countryCode=%s", pincode, countryCode);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<?, ?> json = mapper.readValue(responseBody, Map.class);
                        List<Map<String, Object>> codes = (List<Map<String, Object>>) json.get("postalcodes");
                        if (codes == null || codes.isEmpty()) return;
                        String placeName = (String) codes.get(0).get("placeName");
                        Platform.runLater(() -> cityField.setText(placeName));
                    } catch (Exception ignored) {}
                });
    }

    @FXML
    private void handleChooseLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Logo File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedLogoFile = file;
            logoFilePath = file.getName();
            logoPathLabel.setText(logoFilePath);
            logoPreview.setImage(null);
        }
    }

    @FXML
    private void handleLogoPreviewClick() {
        if (selectedLogoFile != null) {
            Image image = new Image(selectedLogoFile.toURI().toString());
            logoPreview.setImage(image);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("id", parseInt(idField.getText()));
            data.put("fullName", fullNameField.getText());
            data.put("companyName", companyNameField.getText());
            data.put("emailAddress", emailField.getText());
            data.put("companyType", companyTypeField.getText());
            data.put("mobileNumber", parseLong(mobileField.getText()));
            data.put("logo", logoFilePath);
            data.put("gstNumber", gstField.getText());
            data.put("address", addressArea.getText());
            data.put("pincode", parseInt(pincodeField.getText()));
            data.put("city", cityField.getText());

            String rawPass = adminPasswordField.getText();
            if (rawPass != null && !rawPass.isBlank()) {
                data.put("adminPassword", rawPass);
            } else {
                data.put("adminPassword", null);
            }

            Map<String, Object> country = countryComboBox.getValue();
            Map<String, Object> state = stateComboBox.getValue();
            data.put("country", country != null ? country.get("countryName") : null);
            data.put("countryCode", country != null ? country.get("countryCode") : null);
            data.put("state", state != null ? state.get("name") : null);

            if (licenseResponse != null) {
                Map<String, Object> lic = new HashMap<>();
                lic.put("licenseKey", licenseResponse.getLicenseKey());
                lic.put("startDate", licenseResponse.getStartDate());
                lic.put("endDate", licenseResponse.getEndDate());
                data.put("licenseDetails", lic);
            }

            String json = new ObjectMapper().writeValueAsString(data);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiService.getBaseUrl() + "/license/client/save"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                ConfigManager config = ConfigManager.getInstance();
                config.saveClientDetails(true);
                config.markSetupCompleted(true);

                if (licenseResponse != null) {
                    config.saveLicenseDetails(licenseResponse, licenseResponse.getSystemId(), true, true);
                }

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Client Details Saved");
                    alert.setContentText("Client information saved successfully.");
                    alert.showAndWait();
                    openLoginPage();
                });
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Save Failed");
                    alert.setHeaderText("Error Saving Client Details");
                    alert.setContentText("Server returned status: " + response.statusCode());
                    alert.showAndWait();
                });
            }
        } catch (Exception ignored) {}
    }

    private void openLoginPage() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_form.fxml"));
                Parent root = loader.load();
                Stage loginStage = new Stage();
                loginStage.initStyle(StageStyle.DECORATED);
                loginStage.setScene(new Scene(root));
                loginStage.setTitle("Login");
                loginStage.show();
                ((Stage) idField.getScene().getWindow()).close();
            } catch (Exception ignored) {}
        });
    }

    private boolean validateForm() {
        if (companyTypeField.getText().isBlank()) return showValidation("Company Type is required");
        if (mobileField.getText().isBlank()) return showValidation("Mobile Number is required");
        if (gstField.getText().isBlank()) return showValidation("GST Number is required");
        if (addressArea.getText().isBlank()) return showValidation("Address is required");
        if (countryComboBox.getValue() == null) return showValidation("Country is required");
        if (stateComboBox.getValue() == null) return showValidation("State is required");
        if (pincodeField.getText().isBlank()) return showValidation("Pincode is required");
        return true;
    }

    private boolean showValidation(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
        return false;
    }

    private Integer parseInt(String value) {
        try { return Integer.parseInt(value.trim()); } catch (Exception e) { return null; }
    }

    private Long parseLong(String value) {
        try { return Long.parseLong(value.trim()); } catch (Exception e) { return null; }
    }

    @FXML
    private void handleReset() {
        companyTypeField.clear();
        mobileField.clear();
        gstField.clear();
        addressArea.clear();
        pincodeField.clear();
        cityField.clear();
        adminPasswordField.clear();
        logoPathLabel.setText("");
        logoFilePath = "";
        logoPreview.setImage(null);
        countryComboBox.getSelectionModel().clearSelection();
        stateComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleCancel() { ((Stage) idField.getScene().getWindow()).close(); }

    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
