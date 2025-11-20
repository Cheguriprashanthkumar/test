package org.nishgrid.clienterp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.model.SalesReportRow;

import java.time.LocalDate;

public class SalesReportController {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TableView<SalesReportRow> reportTable;
    @FXML private TableColumn<SalesReportRow, LocalDate> dateCol;
    @FXML private TableColumn<SalesReportRow, Integer> totalInvoicesCol;
    @FXML private TableColumn<SalesReportRow, Double> totalAmountCol;
    @FXML private TableColumn<SalesReportRow, Double> netAmountCol;

    private final ObservableList<SalesReportRow> reportData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        totalInvoicesCol.setCellValueFactory(data -> data.getValue().totalInvoicesProperty().asObject());
        totalAmountCol.setCellValueFactory(data -> data.getValue().totalAmountProperty().asObject());
        netAmountCol.setCellValueFactory(data -> data.getValue().netAmountProperty().asObject());
    }

    @FXML
    private void handleGenerateReport() {
        reportData.clear();

        // TODO: Replace with DB query later
        reportData.addAll(
                new SalesReportRow(LocalDate.of(2025, 7, 26), 5, 52000.0, 51500.0),
                new SalesReportRow(LocalDate.of(2025, 7, 27), 3, 30500.0, 29700.0),
                new SalesReportRow(LocalDate.of(2025, 7, 28), 7, 78000.0, 76500.0)
        );

        reportTable.setItems(reportData);
    }
}
