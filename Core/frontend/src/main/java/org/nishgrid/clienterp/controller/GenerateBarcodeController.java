package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.dto.BarcodeGenerationDTO;
import org.nishgrid.clienterp.dto.BarcodeResponseDTO;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.util.Optional;

public class GenerateBarcodeController {

    // --- FXML UI Elements ---
    @FXML private TextField productNameField;
    @FXML private ComboBox<String> itemTypeComboBox;
    @FXML private TextField weightField;
    @FXML private TextField purityField;
    @FXML private TextField hsnCodeField;
    @FXML private TextField purchaseRateField;
    @FXML private TextField makingChargeField;
    @FXML private TextField stoneChargeField;
    @FXML private ComboBox<String> createdForComboBox;
    @FXML private TextField itemIdField;
    @FXML private TextField linkedTransactionIdField;
    @FXML private TextField locationIdField;
    @FXML private TextField generatedByField;
    @FXML private TextArea remarksArea;
    @FXML private ProgressIndicator progressIndicator; // New progress indicator

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        itemTypeComboBox.setItems(FXCollections.observableArrayList(
                "gold_jewellery", "coin", "diamond", "silver", "accessory"
        ));
        createdForComboBox.setItems(FXCollections.observableArrayList(
                "purchase", "stock", "repair", "exchange"
        ));
    }

    @FXML
    private void handleClearForm() {
        productNameField.clear();
        weightField.clear();
        purityField.clear();
        hsnCodeField.clear();
        purchaseRateField.clear();
        makingChargeField.clear();
        stoneChargeField.clear();
        itemIdField.clear();
        linkedTransactionIdField.clear();
        locationIdField.clear();
        generatedByField.clear();
        remarksArea.clear();
        itemTypeComboBox.getSelectionModel().clearSelection();
        createdForComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleGenerateBarcode() {
        if (!validateForm()) {
            return; // Validation failed, showAlert was already called
        }
        try {
            BarcodeGenerationDTO dto = createDtoFromForm();
            progressIndicator.setVisible(true); // Show loading indicator
            submitData(dto);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid number format. Please check all numeric fields.", true);
        }
    }

    private boolean validateForm() {
        if (productNameField.getText().isBlank() || generatedByField.getText().isBlank()
                || itemTypeComboBox.getValue() == null || createdForComboBox.getValue() == null) {
            showAlert("Validation Error", "Product Name, Item Type, Employee ID, and Created For are required fields.", true);
            return false;
        }
        return true;
    }

    private BarcodeGenerationDTO createDtoFromForm() {
        BarcodeGenerationDTO dto = new BarcodeGenerationDTO();
        dto.setProductName(productNameField.getText());
        dto.setItemType(itemTypeComboBox.getValue());
        dto.setCreatedFor(createdForComboBox.getValue());
        dto.setPurity(purityField.getText());
        dto.setHsnCode(hsnCodeField.getText());
        dto.setRemarks(remarksArea.getText());
        dto.setWeight(parseBigDecimal(weightField.getText()));
        dto.setPurchaseRate(parseBigDecimal(purchaseRateField.getText()));
        dto.setMakingCharge(parseBigDecimal(makingChargeField.getText()));
        dto.setStoneCharge(parseBigDecimal(stoneChargeField.getText()));
        dto.setItemId(parseLong(itemIdField.getText()));
        dto.setLinkedTransactionId(parseLong(linkedTransactionIdField.getText()));
        dto.setLocationId(parseLong(locationIdField.getText()));
        dto.setGeneratedBy(new BarcodeGenerationDTO.EmployeeDTO(parseInteger(generatedByField.getText())));
        return dto;
    }

    private void submitData(BarcodeGenerationDTO dto) {
        apiService.generateBarcode(dto)
                .thenAccept(response -> Platform.runLater(() -> {
                    progressIndicator.setVisible(false); // Hide loading indicator
                    showAlert("Success", "Barcode created successfully!\nValue: " + response.getBarcodeValue(), false);
                    handleClearForm(); // Clear the form on success
                    showPrintLabelWindow(response);
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false); // Hide loading indicator
                        showAlert("API Error", "Failed to create barcode: " + ex.getCause().getMessage(), true);
                    });
                    return null;
                });
    }

    private void showPrintLabelWindow(BarcodeResponseDTO barcodeData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/print-label-view.fxml"));
            VBox root = loader.load();

            PrintLabelController controller = loader.getController();
            controller.populateLabel(barcodeData);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Print Barcode Label");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("UI Error", "Could not open the print preview window.", true);
        }
    }

    // New method for showing alerts
    private void showAlert(String title, String message, boolean isError) {
        Alert.AlertType type = isError ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- Helper methods for safe parsing ---
    private BigDecimal parseBigDecimal(String value) {
        return (value == null || value.isBlank()) ? null : new BigDecimal(value);
    }

    private Long parseLong(String value) {
        return (value == null || value.isBlank()) ? null : Long.parseLong(value);
    }

    private Integer parseInteger(String value) {
        return (value == null || value.isBlank()) ? null : Integer.parseInt(value);
    }
}