package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.OtherExpenseExchangeRequest;
import org.nishgrid.clienterp.dto.OtherExpenseExchangeResponse;
import org.nishgrid.clienterp.dto.OtherExpenseResponse;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OtherExpenseExchangeController {

    private final ApiService apiService = new ApiService();
    private OtherExpenseExchangeResponse selectedExchange = null;

    @FXML private ComboBox<OtherExpenseResponse> oldExpenseComboBox;
    @FXML private ComboBox<OtherExpenseResponse> newExpenseComboBox;
    @FXML private DatePicker exchangeDatePicker;
    @FXML private ComboBox<Vendor> vendorComboBox;
    @FXML private TextField adjustedAmountField;
    @FXML private TextArea reasonArea;

    @FXML private TableView<OtherExpenseExchangeResponse> exchangeTable;
    @FXML private TableColumn<OtherExpenseExchangeResponse, LocalDate> colExchangeDate;
    @FXML private TableColumn<OtherExpenseExchangeResponse, String> colOldExpense;
    @FXML private TableColumn<OtherExpenseExchangeResponse, String> colNewExpense;
    @FXML private TableColumn<OtherExpenseExchangeResponse, BigDecimal> colAdjustedAmount;
    @FXML private TableColumn<OtherExpenseExchangeResponse, String> colReason;

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTable();
        loadInitialData();
    }

    private void setupComboBoxes() {
        StringConverter<OtherExpenseResponse> expenseConverter = new StringConverter<>() {
            @Override public String toString(OtherExpenseResponse exp) { return exp != null ? exp.getExpenseCategory() + " (" + exp.getPaidTo() + ")" : ""; }
            @Override public OtherExpenseResponse fromString(String s) { return null; }
        };
        oldExpenseComboBox.setConverter(expenseConverter);
        newExpenseComboBox.setConverter(expenseConverter);

        vendorComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Vendor v) { return v != null ? v.getName() : ""; }
            @Override public Vendor fromString(String s) { return null; }
        });
    }

    private void setupTable() {
        colExchangeDate.setCellValueFactory(new PropertyValueFactory<>("exchangeDate"));
        colOldExpense.setCellValueFactory(new PropertyValueFactory<>("oldExpenseCategory"));
        colNewExpense.setCellValueFactory(new PropertyValueFactory<>("newExpenseCategory"));
        colAdjustedAmount.setCellValueFactory(new PropertyValueFactory<>("adjustedAmount"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
    }

    private void loadInitialData() {
        apiService.getAllExpenses().thenAccept(expenses -> Platform.runLater(() -> {
            oldExpenseComboBox.setItems(FXCollections.observableArrayList(expenses));
            newExpenseComboBox.setItems(FXCollections.observableArrayList(expenses));
        }));
        apiService.getVendors().thenAccept(vendors -> Platform.runLater(() -> vendorComboBox.setItems(FXCollections.observableArrayList(vendors))));
        loadAllExchanges();
    }

    private void loadAllExchanges() {
        apiService.getAllExchanges().thenAccept(exchanges -> Platform.runLater(() -> exchangeTable.setItems(FXCollections.observableArrayList(exchanges))));
    }

    @FXML
    private void handleSaveExchange() {
        if (oldExpenseComboBox.getSelectionModel().isEmpty() || newExpenseComboBox.getSelectionModel().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Old and New expenses must be selected.").show();
            return;
        }

        OtherExpenseExchangeRequest request = new OtherExpenseExchangeRequest();
        request.setOldExpenseId(oldExpenseComboBox.getValue().getExpenseId());
        request.setNewExpenseId(newExpenseComboBox.getValue().getExpenseId());
        request.setExchangeDate(exchangeDatePicker.getValue());
        if (vendorComboBox.getValue() != null) {
            request.setVendorId(vendorComboBox.getValue().getId());
        }
        request.setAdjustedAmount(!adjustedAmountField.getText().isBlank() ? new BigDecimal(adjustedAmountField.getText()) : null);
        request.setReason(reasonArea.getText());
        request.setCreatedBy("admin");
        request.setApprovedBy("manager");

        apiService.createExchange(request).thenRun(() -> Platform.runLater(this::refreshData));
    }

    @FXML
    private void handleDeleteExchange() {
        selectedExchange = exchangeTable.getSelectionModel().getSelectedItem();
        if (selectedExchange == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an exchange to delete.").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Delete this exchange record?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteExchange(selectedExchange.getExchangeId()).thenRun(() -> Platform.runLater(this::refreshData));
            }
        });
    }

    @FXML
    private void handleClearForm() {
        oldExpenseComboBox.getSelectionModel().clearSelection();
        newExpenseComboBox.getSelectionModel().clearSelection();
        exchangeDatePicker.setValue(null);
        vendorComboBox.getSelectionModel().clearSelection();
        adjustedAmountField.clear();
        reasonArea.clear();
    }

    private void refreshData() {
        loadAllExchanges();
        handleClearForm();
    }
}