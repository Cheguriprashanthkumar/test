package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nishgrid.clienterp.model.ProductCatalog;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.service.ApiService;

public class VendorProductController {

    private final ApiService apiService = new ApiService();

    private Vendor selectedVendor = null;
    private ProductCatalog selectedProduct = null;

    // --- Vendor Fields ---
    @FXML private TableView<Vendor> vendorTable;
    @FXML private TableColumn<Vendor, Long> vendorIdCol;
    @FXML private TableColumn<Vendor, String> vendorNameCol;
    @FXML private TableColumn<Vendor, String> vendorEmailCol;
    @FXML private TableColumn<Vendor, String> vendorPhoneCol;
    @FXML private TableColumn<Vendor, String> vendorContactPersonCol;
    @FXML private TableColumn<Vendor, String> vendorGstNumberCol;
    @FXML private TableColumn<Vendor, String> vendorAddressCol;
    @FXML private TableColumn<Vendor, String> vendorStateCol; // ADDED
    @FXML private TextField vendorNameField, vendorEmailField, vendorPhoneField;
    @FXML private TextField vendorGstNumberField, vendorContactPersonField, vendorAddressField;
    @FXML private TextField vendorStateField; // ADDED

    // --- Product Fields ---
    @FXML private TableView<ProductCatalog> productTable;
    @FXML private TableColumn<ProductCatalog, Long> productIdCol;
    @FXML private TableColumn<ProductCatalog, String> productNameCol;
    @FXML private TableColumn<ProductCatalog, String> productPurityCol;
    @FXML private TableColumn<ProductCatalog, String> productRateCol;
    @FXML private TextField productNameField, productPurityField, productRateField;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSelectionListeners();
        loadDataFromBackend();
    }

    private void setupTableColumns() {
        // Vendor columns
        vendorIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        vendorNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        vendorEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        vendorPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        vendorContactPersonCol.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        vendorGstNumberCol.setCellValueFactory(new PropertyValueFactory<>("gstNumber"));
        vendorAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        vendorStateCol.setCellValueFactory(new PropertyValueFactory<>("state")); // ADDED

        // Product columns
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPurityCol.setCellValueFactory(new PropertyValueFactory<>("purity"));
        productRateCol.setCellValueFactory(new PropertyValueFactory<>("defaultRate"));
    }

    private void setupSelectionListeners() {
        vendorTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedVendor = newSelection;
                    if (newSelection != null) {
                        vendorNameField.setText(selectedVendor.getName());
                        vendorEmailField.setText(selectedVendor.getEmail());
                        vendorPhoneField.setText(selectedVendor.getPhone());
                        vendorGstNumberField.setText(selectedVendor.getGstNumber());
                        vendorContactPersonField.setText(selectedVendor.getContactPerson());
                        vendorAddressField.setText(selectedVendor.getAddress());
                        vendorStateField.setText(selectedVendor.getState()); // ADDED
                    }
                });

        productTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedProduct = newSelection;
                    if (newSelection != null) {
                        productNameField.setText(selectedProduct.getName());
                        productPurityField.setText(selectedProduct.getPurity());
                        productRateField.setText(selectedProduct.getDefaultRate());
                    }
                });
    }

    private void loadDataFromBackend() {
        apiService.getVendors().thenAccept(vendors ->
                Platform.runLater(() -> vendorTable.setItems(FXCollections.observableArrayList(vendors)))
        );
        apiService.getProducts().thenAccept(products ->
                Platform.runLater(() -> productTable.setItems(FXCollections.observableArrayList(products)))
        );
    }

    @FXML
    private void saveVendor() {
        String name = vendorNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert("Validation Error", "Vendor Name cannot be empty.");
            return;
        }

        // Use setters on a mutable Vendor object
        Vendor vendorToSave;
        if (selectedVendor == null) {
            vendorToSave = new Vendor(); // Creating a new vendor
        } else {
            vendorToSave = selectedVendor; // Editing an existing vendor
        }

        // Populate the object with data from the form fields
        vendorToSave.setName(vendorNameField.getText());
        vendorToSave.setEmail(vendorEmailField.getText());
        vendorToSave.setPhone(vendorPhoneField.getText());
        vendorToSave.setGstNumber(vendorGstNumberField.getText());
        vendorToSave.setContactPerson(vendorContactPersonField.getText());
        vendorToSave.setAddress(vendorAddressField.getText());
        vendorToSave.setState(vendorStateField.getText()); // ADDED

        if (selectedVendor == null) {
            apiService.createVendor(vendorToSave).thenRun(this::refreshVendorTable);
        } else {
            apiService.updateVendor(selectedVendor.getId(), vendorToSave).thenRun(this::refreshVendorTable);
        }
    }

    @FXML
    private void deleteSelectedVendor() {
        if (selectedVendor != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedVendor.getName() + "?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    apiService.deleteVendor(selectedVendor.getId()).thenRun(this::refreshVendorTable);
                }
            });
        } else {
            new Alert(Alert.AlertType.WARNING, "Please select a vendor to delete.").show();
        }
    }

    @FXML
    private void clearVendorForm() {
        selectedVendor = null;
        vendorTable.getSelectionModel().clearSelection();
        vendorNameField.clear();
        vendorEmailField.clear();
        vendorPhoneField.clear();
        vendorGstNumberField.clear();
        vendorContactPersonField.clear();
        vendorAddressField.clear();
        vendorStateField.clear(); // ADDED
        vendorNameField.requestFocus();
    }

    private void refreshVendorTable() {
        Platform.runLater(() -> {
            apiService.getVendors().thenAccept(vendors ->
                    Platform.runLater(() -> vendorTable.setItems(FXCollections.observableArrayList(vendors)))
            );
            clearVendorForm();
        });
    }

    @FXML
    private void saveProduct() {
        String name = productNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert("Validation Error", "Product Name cannot be empty.");
            return;
        }

        ProductCatalog productToSave = new ProductCatalog(
                selectedProduct != null ? selectedProduct.getId() : 0,
                name,
                productPurityField.getText(),
                productRateField.getText()
        );

        if (selectedProduct == null) {
            apiService.createProduct(productToSave).thenRun(this::refreshProductTable);
        } else {
            apiService.updateProduct(selectedProduct.getId(), productToSave).thenRun(this::refreshProductTable);
        }
    }

    @FXML
    private void deleteSelectedProduct() {
        if (selectedProduct != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedProduct.getName() + "?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    apiService.deleteProduct(selectedProduct.getId()).thenRun(this::refreshProductTable);
                }
            });
        } else {
            new Alert(Alert.AlertType.WARNING, "Please select a product to delete.").show();
        }
    }

    @FXML
    private void clearProductForm() {
        selectedProduct = null;
        productTable.getSelectionModel().clearSelection();
        productNameField.clear();
        productPurityField.clear();
        productRateField.clear();
        productNameField.requestFocus();
    }

    private void refreshProductTable() {
        Platform.runLater(() -> {
            apiService.getProducts().thenAccept(products ->
                    Platform.runLater(() -> productTable.setItems(FXCollections.observableArrayList(products)))
            );
            clearProductForm();
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}