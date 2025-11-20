package org.nishgrid.clienterp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nishgrid.clienterp.model.CompanyDetails;
import org.nishgrid.clienterp.service.ApiService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class CompanyDetailsViewController {

    @FXML private TableView<CompanyDetails> detailsTableView;
    @FXML private TableColumn<CompanyDetails, String> nameColumn;
    @FXML private TableColumn<CompanyDetails, String> addressColumn;
    @FXML private TableColumn<CompanyDetails, List<String>> contactsColumn;
    @FXML private TableColumn<CompanyDetails, String> taglineColumn;
    @FXML private TableColumn<CompanyDetails, Boolean> activeColumn;

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextArea contactsArea;
    @FXML private TextField taglineField;
    @FXML private CheckBox activeCheckBox;

    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;

    private final ApiService clientService = new ApiService();

    private final ObservableList<CompanyDetails> detailsList = FXCollections.observableArrayList();
    private CompanyDetails selectedDetails = null;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("companyAddress"));

        // --- THIS IS THE FIX ---
        taglineColumn.setCellValueFactory(new PropertyValueFactory<>("companyTagline"));
        // --- END OF FIX ---

        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        contactsColumn.setCellValueFactory(new PropertyValueFactory<>("companyContacts"));
        contactsColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(List<String> items, boolean empty) {
                super.updateItem(items, empty);
                if (empty || items == null || items.isEmpty()) {
                    setText(null);
                } else {
                    setText(String.join(", ", items));
                }
            }
        });

        detailsTableView.setItems(detailsList);

        detailsTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> populateForm(newSelection)
        );

        loadDetails();
    }

    private void loadDetails() {
        clientService.getAllCompanyDetails()
                .thenAccept(details -> {
                    detailsList.setAll(details);
                    detailsTableView.refresh();
                })
                .exceptionally(e -> {
                    showError("Failed to load company details: " + e.getMessage());
                    return null;
                });
    }

    private void populateForm(CompanyDetails details) {
        this.selectedDetails = details;
        if (details != null) {
            nameField.setText(details.getCompanyName());
            addressField.setText(details.getCompanyAddress());
            taglineField.setText(details.getCompanyTagline());
            activeCheckBox.setSelected(details.isActive());

            if (details.getCompanyContacts() != null) {
                contactsArea.setText(String.join("\n", details.getCompanyContacts()));
            } else {
                contactsArea.clear();
            }

            deleteButton.setDisable(false);
        } else {
            clearForm();
        }
    }

    @FXML
    private void handleNew() {
        clearForm();
    }

    @FXML
    private void handleSave() {
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            showError("Company Name is required.");
            return;
        }

        CompanyDetails details = (selectedDetails != null) ? selectedDetails : new CompanyDetails();
        details.setCompanyName(nameField.getText());
        details.setCompanyAddress(addressField.getText());
        details.setTagline(taglineField.getText());
        details.setActive(activeCheckBox.isSelected());

        List<String> contacts = Arrays.stream(contactsArea.getText().split("\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        details.setCompanyContacts(contacts);

        CompletableFuture<CompanyDetails> saveFuture;
        if (details.getId() != null) {
            saveFuture = clientService.updateCompanyDetails(details.getId(), details);
        } else {
            saveFuture = clientService.createCompanyDetails(details);
        }

        saveFuture.thenAccept(savedDetails -> {
                    loadDetails();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Company details saved successfully.");
                })
                .exceptionally(e -> {
                    showError("Failed to save details: " + e.getMessage());
                    return null;
                });
    }

    @FXML
    private void handleDelete() {
        if (selectedDetails == null) {
            showError("No item selected to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Company Profile");
        alert.setContentText("Are you sure you want to delete '" + selectedDetails.getCompanyName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            clientService.deleteCompanyDetails(selectedDetails.getId())
                    .thenAccept(v -> {
                        loadDetails();
                        clearForm();
                    })
                    .exceptionally(e -> {
                        showError("Failed to delete details: " + e.getMessage());
                        return null;
                    });
        }
    }

    private void clearForm() {
        selectedDetails = null;
        nameField.clear();
        addressField.clear();
        contactsArea.clear();
        taglineField.clear();
        activeCheckBox.setSelected(false);
        detailsTableView.getSelectionModel().clearSelection();
        deleteButton.setDisable(true);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
