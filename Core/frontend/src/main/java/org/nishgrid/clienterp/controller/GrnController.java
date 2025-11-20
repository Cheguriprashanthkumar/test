package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.GrnRequest;
import org.nishgrid.clienterp.model.GoodsReceiptNote;
import org.nishgrid.clienterp.model.PurchaseOrder;
import org.nishgrid.clienterp.service.ApiService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GrnController {

    private final ApiService apiService = new ApiService();
    private GoodsReceiptNote selectedGrn = null;

    @FXML private ComboBox<PurchaseOrder> poComboBox;
    @FXML private TextField grnNumberField;
    @FXML private DatePicker receivedDatePicker;
    @FXML private TextField receivedByField;
    @FXML private TextField remarksField;

    @FXML private TableView<GoodsReceiptNote> grnTable;
    @FXML private TableColumn<GoodsReceiptNote, String> colGrnNumber;
    @FXML private TableColumn<GoodsReceiptNote, String> colPoNumber;
    @FXML private TableColumn<GoodsReceiptNote, String> colVendor;
    @FXML private TableColumn<GoodsReceiptNote, LocalDate> colReceivedDate;
    @FXML private TableColumn<GoodsReceiptNote, String> colReceivedBy;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        setupTableSelectionListener();
        loadInitialData();
    }

    private void setupComboBox() {
        poComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(PurchaseOrder po) {
                return po != null ? po.getPoNumber() + " (" + po.getVendor().getName() + ")" : "";
            }
            @Override public PurchaseOrder fromString(String string) { return null; }
        });
    }

    private void setupTable() {
        colGrnNumber.setCellValueFactory(new PropertyValueFactory<>("grnNumber"));
        colPoNumber.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getPurchaseOrder().getPoNumber()));
        colVendor.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getPurchaseOrder().getVendor().getName()));
        colReceivedDate.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
        colReceivedBy.setCellValueFactory(new PropertyValueFactory<>("receivedBy"));
    }

    private void setupTableSelectionListener() {
        grnTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedGrn = newSelection;
            if (newSelection != null) {
                poComboBox.getSelectionModel().select(newSelection.getPurchaseOrder());
                grnNumberField.setText(newSelection.getGrnNumber());
                receivedDatePicker.setValue(newSelection.getReceivedDate());
                receivedByField.setText(newSelection.getReceivedBy());
                remarksField.setText(newSelection.getRemarks());
                poComboBox.setDisable(true);
            }
        });
    }

    private void loadInitialData() {
        apiService.getAllPurchaseOrders().thenAccept(allPOs -> {
            List<PurchaseOrder> pendingPOs = allPOs.stream()
                    .filter(po -> "PENDING".equalsIgnoreCase(po.getStatus()))
                    .collect(Collectors.toList());
            Platform.runLater(() -> poComboBox.setItems(FXCollections.observableArrayList(pendingPOs)));
        });
        loadAllGrns();
    }

    private void loadAllGrns() {
        apiService.getAllGrns().thenAccept(grns -> Platform.runLater(() -> grnTable.setItems(FXCollections.observableArrayList(grns))));
    }

    @FXML
    private void handleSaveGrn() {
        GrnRequest request = new GrnRequest();

        // When updating, the Purchase Order doesn't change and the combo box is disabled.
        // So, we only need to validate the combo box when creating a NEW GRN.
        if (selectedGrn == null) {
            if (poComboBox.getSelectionModel().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "A Purchase Order must be selected.").show();
                return;
            }
            request.setPurchaseOrderId(poComboBox.getSelectionModel().getSelectedItem().getId());
        }

        // This validation applies to both creating and updating.
        if (grnNumberField.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "GRN Number is required.").show();
            return;
        }

        // Set the rest of the request details from the form fields.
        request.setGrnNumber(grnNumberField.getText());
        request.setReceivedDate(receivedDatePicker.getValue());
        request.setReceivedBy(receivedByField.getText());
        request.setRemarks(remarksField.getText());

        // Check if we are creating or updating.
        if (selectedGrn == null) {
            apiService.createGrn(request).thenRun(() -> Platform.runLater(this::refreshData));
        } else {
            apiService.updateGrn(selectedGrn.getId(), request).thenRun(() -> Platform.runLater(this::refreshData));
        }
    }

    @FXML
    private void handleDeleteGrn() {
        GoodsReceiptNote selected = grnTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a GRN to delete.").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getGrnNumber() + "? This will revert the PO status to PENDING.");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteGrn(selected.getId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        selectedGrn = null;
        grnTable.getSelectionModel().clearSelection();
        poComboBox.getSelectionModel().clearSelection();
        grnNumberField.clear();
        receivedDatePicker.setValue(null);
        receivedByField.clear();
        remarksField.clear();
        poComboBox.setDisable(false);
    }

    private void refreshData() {
        loadInitialData();
        handleClearForm();
    }
}