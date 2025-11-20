package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.nishgrid.clienterp.dto.BarcodeRequest;
import org.nishgrid.clienterp.dto.BarcodeResponse;
import org.nishgrid.clienterp.model.GoodsReceiptNote;
import org.nishgrid.clienterp.model.PurchaseOrderItem;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BarcodeController {

    public static class BarcodeEntry {
        private final PurchaseOrderItem originalItem;
        private final SimpleStringProperty barcodeNo = new SimpleStringProperty();
        private final SimpleStringProperty scannedBy = new SimpleStringProperty();

        public BarcodeEntry(PurchaseOrderItem originalItem) { this.originalItem = originalItem; }
        public String getItemName() { return originalItem.getProductName(); }
        public BigDecimal getWeight() { return originalItem.getWeight(); }
        public String getBarcodeNo() { return barcodeNo.get(); }
        public void setBarcodeNo(String barcodeNo) { this.barcodeNo.set(barcodeNo); }
        public SimpleStringProperty barcodeNoProperty() { return barcodeNo; }
        public String getScannedBy() { return scannedBy.get(); }
        public void setScannedBy(String scannedBy) { this.scannedBy.set(scannedBy); }
        public SimpleStringProperty scannedByProperty() { return scannedBy; }
    }

    private final ApiService apiService = new ApiService();
    private final ObservableList<BarcodeEntry> barcodeEntries = FXCollections.observableArrayList();

    @FXML private ComboBox<GoodsReceiptNote> grnComboBox;
    @FXML private TableView<BarcodeEntry> itemBarcodeTable;
    @FXML private TableColumn<BarcodeEntry, String> colItemName;
    @FXML private TableColumn<BarcodeEntry, BigDecimal> colItemWeight;
    @FXML private TableColumn<BarcodeEntry, String> colBarcode;
    @FXML private TableColumn<BarcodeEntry, String> colScannedBy;

    @FXML
    public void initialize() {
        setupComboBox();
        setupTable();
        loadInitialData();
    }

    private void setupComboBox() {
        grnComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(GoodsReceiptNote grn) { return grn != null ? grn.getGrnNumber() : ""; }
            @Override public GoodsReceiptNote fromString(String s) { return null; }
        });

        grnComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, grn) -> {
            if (grn != null) {
                populateTableForGrn(grn);
            } else {
                barcodeEntries.clear();
            }
        });
    }

    private void populateTableForGrn(GoodsReceiptNote grn) {
        List<PurchaseOrderItem> itemsFromPo = grn.getPurchaseOrder().getItems();
        apiService.getBarcodesByGrnId(grn.getId()).thenAccept(savedBarcodes -> {
            Map<String, BarcodeResponse> savedBarcodeMap = savedBarcodes.stream()
                    .collect(Collectors.toMap(BarcodeResponse::getItemName, b -> b, (first, second) -> first));

            List<BarcodeEntry> finalEntries = itemsFromPo.stream().map(item -> {
                BarcodeEntry entry = new BarcodeEntry(item);
                if (savedBarcodeMap.containsKey(item.getProductName())) {
                    BarcodeResponse saved = savedBarcodeMap.get(item.getProductName());
                    entry.setBarcodeNo(saved.getBarcodeNo());
                    entry.setScannedBy(saved.getScannedBy());
                }
                return entry;
            }).collect(Collectors.toList());

            Platform.runLater(() -> barcodeEntries.setAll(finalEntries));
        });
    }

    private void setupTable() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colItemWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));

        // THIS IS THE FIX: Use the property directly for both columns
        colBarcode.setCellValueFactory(cellData -> cellData.getValue().barcodeNoProperty());
        colScannedBy.setCellValueFactory(cellData -> cellData.getValue().scannedByProperty());

        colBarcode.setCellFactory(TextFieldTableCell.forTableColumn());
        colBarcode.setOnEditCommit(event -> {
            BarcodeEntry entry = event.getRowValue();
            entry.setBarcodeNo(event.getNewValue());
        });

        itemBarcodeTable.setItems(barcodeEntries);
    }

    private void loadInitialData() {
        apiService.getAllGrns().thenAccept(grns -> Platform.runLater(() -> grnComboBox.setItems(FXCollections.observableArrayList(grns))));
    }

    @FXML
    private void handleSaveBarcodes() {
        GoodsReceiptNote selectedGrn = grnComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrn == null || barcodeEntries.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please select a GRN and ensure it has items.").show();
            return;
        }

        BarcodeRequest request = new BarcodeRequest();
        request.setGrnId(selectedGrn.getId());
        request.setScannedBy("admin");

        request.setBarcodes(barcodeEntries.stream()
                .filter(entry -> entry.getBarcodeNo() != null && !entry.getBarcodeNo().isBlank())
                .map(entry -> {
                    BarcodeRequest.BarcodeItemDTO dto = new BarcodeRequest.BarcodeItemDTO();
                    dto.setItemName(entry.getItemName());
                    dto.setBarcodeNo(entry.getBarcodeNo());
                    dto.setWeight(entry.getWeight());
                    return dto;
                }).collect(Collectors.toList()));

        if (request.getBarcodes().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No barcode numbers were entered to save.").show();
            return;
        }

        apiService.createBarcodes(request).thenAccept(response -> {
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.INFORMATION, response.size() + " barcodes saved successfully for GRN " + selectedGrn.getGrnNumber()).show();
                populateTableForGrn(selectedGrn);
            });
        });
    }
}