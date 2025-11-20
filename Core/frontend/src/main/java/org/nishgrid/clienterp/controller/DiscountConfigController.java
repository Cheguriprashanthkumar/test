package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.model.Discount; // Your JavaFX Model
import org.nishgrid.clienterp.dto.DiscountDTO; // The DTO for API communication
import org.nishgrid.clienterp.service.ApiService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class DiscountConfigController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> valueTypeBox;
    @FXML private TextArea descriptionArea;
    @FXML private TableView<Discount> discountsTable;
    @FXML private TableColumn<Discount, Number> idCol;
    @FXML private TableColumn<Discount, String> titleCol;
    @FXML private TableColumn<Discount, String> typeCol;
    @FXML private TableColumn<Discount, String> descCol;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String API_BASE_URL = ApiService.getBaseUrl()+"/discounts";

    @FXML
    public void initialize() {
        valueTypeBox.setItems(FXCollections.observableArrayList("percent", "fixed"));

        idCol.setCellValueFactory(data -> data.getValue().discountIdProperty());
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());
        typeCol.setCellValueFactory(data -> data.getValue().valueTypeProperty());
        descCol.setCellValueFactory(data -> data.getValue().valueDescriptionProperty());

        // Add a listener to populate the form when a row is selected
        discountsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateForm(newSelection);
                    }
                });

        loadDiscounts();
    }

    private void loadDiscounts() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<DiscountDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
                        List<Discount> fxList = dtoList.stream()
                                .map(dto -> new Discount(dto.getDiscountId().intValue(), dto.getTitle(), dto.getValueType(), dto.getValueDescription()))
                                .collect(Collectors.toList());

                        Platform.runLater(() -> discountsTable.setItems(FXCollections.observableArrayList(fxList)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @FXML
    private void handleAddDiscount() {
        DiscountDTO dto = createDtoFromForm();
        if (dto == null) return;

        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            sendRequestAndRefresh(request, "Discount added successfully!");
        } catch (Exception e) {
            showAlert("Error", "Could not create discount: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateDiscount() {
        Discount selected = discountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a discount to update.");
            return;
        }

        DiscountDTO dto = createDtoFromForm();
        if (dto == null) return;

        try {
            String requestBody = objectMapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/" + selected.getDiscountId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            sendRequestAndRefresh(request, "Discount updated successfully!");
        } catch (Exception e) {
            showAlert("Error", "Could not update discount: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteDiscount() {
        Discount selected = discountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a discount to delete.");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/" + selected.getDiscountId()))
                .DELETE()
                .build();

        sendRequestAndRefresh(request, "Discount deleted successfully!");
    }

    private void sendRequestAndRefresh(HttpRequest request, String successMessage) {
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        showAlert("Success", successMessage);
                        loadDiscounts();
                        clearForm();
                    } else {
                        showAlert("Error", "Operation failed. Server responded with: " + response.statusCode());
                    }
                })).exceptionally(e -> {
                    Platform.runLater(() -> showAlert("Connection Error", "Could not connect to the server: " + e.getMessage()));
                    return null;
                });
    }

    private DiscountDTO createDtoFromForm() {
        if (titleField.getText().isEmpty() || valueTypeBox.getValue() == null) {
            showAlert("Warning", "Title and Value Type are required.");
            return null;
        }
        DiscountDTO dto = new DiscountDTO();
        dto.setTitle(titleField.getText());
        dto.setValueType(valueTypeBox.getValue());
        dto.setValueDescription(descriptionArea.getText());
        return dto;
    }

    private void populateForm(Discount discount) {
        titleField.setText(discount.getTitle());
        valueTypeBox.setValue(discount.getValueType());
        descriptionArea.setText(discount.getValueDescription());
    }

    private void clearForm() {
        titleField.clear();
        valueTypeBox.setValue(null);
        descriptionArea.clear();
        discountsTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg) {
        Alert.AlertType type = title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        if(title.equals("Warning")) type = Alert.AlertType.WARNING;

        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}