package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.CustomerDTO;
import org.nishgrid.clienterp.dto.CustomerKycRequestDTO;
import org.nishgrid.clienterp.dto.CustomerKycResponseDTO;
import org.nishgrid.clienterp.model.CustomerKycFx;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomerKycController {

    @FXML private ComboBox<CustomerDTO> customerComboBox;
    @FXML private TextField panField, aadhaarField, photoPathField, verifiedByField, verifiedOnField;
    @FXML private TextArea remarksField;
    @FXML private TableView<CustomerKycFx> kycTable;
    @FXML private TableColumn<CustomerKycFx, Number> idCol, customerIdCol;
    @FXML private TableColumn<CustomerKycFx, String> customerNameCol, panCol, aadhaarCol, verifiedCol, verifiedOnCol, remarksCol;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_BASE_URL = ApiService.getBaseUrl() + "/kyc";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Regex patterns for validation
    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("\\d{12}");

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> data.getValue().kycIdProperty());
        customerIdCol.setCellValueFactory(data -> data.getValue().customerIdProperty());
        customerNameCol.setCellValueFactory(data -> data.getValue().customerNameProperty());
        panCol.setCellValueFactory(data -> data.getValue().panNumberProperty());
        aadhaarCol.setCellValueFactory(data -> data.getValue().aadhaarNumberProperty());
        verifiedCol.setCellValueFactory(data -> data.getValue().verifiedByProperty());
        verifiedOnCol.setCellValueFactory(data -> data.getValue().verifiedOnProperty());
        remarksCol.setCellValueFactory(data -> data.getValue().remarksProperty());

        kycTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newKyc) -> { if (newKyc != null) populateForm(newKyc); });

        configureComboBox();
        loadCustomers();
        loadAllKycData();
    }

    private void configureComboBox() {
        customerComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(CustomerDTO c) { return c == null ? null : c.getName() + " (ID: " + c.getId() + ")"; }
            @Override public CustomerDTO fromString(String s) { return null; }
        });
    }

    private void loadCustomers() {
        String url = ApiService.getBaseUrl() + "/customers/selection";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<CustomerDTO> customers = objectMapper.readValue(body, new TypeReference<>() {});
                        Platform.runLater(() -> customerComboBox.setItems(FXCollections.observableArrayList(customers)));
                    } catch (Exception e) { e.printStackTrace(); }
                });
    }

    private void loadAllKycData() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<CustomerKycResponseDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
                        List<CustomerKycFx> fxList = dtoList.stream().map(this::dtoToFxModel).collect(Collectors.toList());
                        Platform.runLater(() -> {
                            kycTable.setItems(FXCollections.observableArrayList(fxList));
                            clearForm();
                        });
                    } catch (Exception e) { e.printStackTrace(); }
                });
    }

    @FXML
    private void handleSave() {
        CustomerDTO selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "A customer must be selected.");
            return;
        }
        try {
            CustomerKycRequestDTO dto = createDtoFromForm();
            if (dto == null) return; // validation failed

            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/" + selectedCustomer.getId()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            sendRequestAndRefresh(request, "KYC saved successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save KYC: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        CustomerKycFx selectedKyc = kycTable.getSelectionModel().getSelectedItem();
        if (selectedKyc == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a KYC record to delete.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete KYC for " + selectedKyc.getCustomerName() + "?", ButtonType.YES, ButtonType.NO);
        if (confirmation.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            long customerId = selectedKyc.getCustomerId();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/" + customerId)).DELETE().build();
            sendRequestAndRefresh(request, "KYC deleted successfully!");
        }
    }

    @FXML
    private void handleBrowsePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(photoPathField.getScene().getWindow());
        if (selectedFile != null) {
            photoPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void sendRequestAndRefresh(HttpRequest request, String successMessage) {
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", successMessage);
                        loadAllKycData();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Request Failed", "Server responded with status " + response.statusCode() + ":\n" + response.body());
                    }
                }));
    }

    private CustomerKycRequestDTO createDtoFromForm() {
        String pan = panField.getText().trim();
        String aadhaar = aadhaarField.getText().trim();

        if (pan.isEmpty() || aadhaar.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "PAN and Aadhaar numbers are required.");
            return null;
        }
        if (!PAN_PATTERN.matcher(pan).matches()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid PAN format.\nFormat: 5 uppercase letters, 4 digits, 1 uppercase letter (e.g., ABCDE1234F).");
            return null;
        }
        if (!AADHAAR_PATTERN.matcher(aadhaar).matches()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid Aadhaar number.\nIt must be exactly 12 digits.");
            return null;
        }

        CustomerKycRequestDTO dto = new CustomerKycRequestDTO();
        dto.setPanNumber(pan);
        dto.setAadhaarNumber(aadhaar);
        dto.setPhotoPath(photoPathField.getText());
        dto.setRemarks(remarksField.getText());
        return dto;
    }

    private void populateForm(CustomerKycFx kyc) {
        panField.setText(kyc.getPanNumber());
        aadhaarField.setText(kyc.getAadhaarNumber());
        photoPathField.setText(kyc.getPhotoPath());
        verifiedByField.setText(kyc.getVerifiedBy());
        verifiedOnField.setText(kyc.getVerifiedOn());
        remarksField.setText(kyc.getRemarks());
        customerComboBox.getItems().stream()
                .filter(c -> Objects.equals(c.getId(), kyc.getCustomerId()))
                .findFirst()
                .ifPresent(customerComboBox.getSelectionModel()::select);
        customerComboBox.setDisable(true);
    }

    @FXML
    private void clearForm() {
        kycTable.getSelectionModel().clearSelection();
        customerComboBox.getSelectionModel().clearSelection();
        customerComboBox.setDisable(false);
        panField.clear();
        aadhaarField.clear();
        photoPathField.clear();
        verifiedByField.clear();
        verifiedOnField.clear();
        remarksField.clear();
    }

    private CustomerKycFx dtoToFxModel(CustomerKycResponseDTO dto) {
        return new CustomerKycFx(
                dto.getKycId(),
                dto.getCustomerId(),
                dto.getCustomerName(),
                dto.getPanNumber(),
                dto.getAadhaarNumber(),
                dto.getPhotoPath(),
                dto.getVerifiedBy(),
                dto.getVerifiedOn() != null ? dto.getVerifiedOn().format(formatter) : "",
                dto.getRemarks()
        );
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
