package org.nishgrid.clienterp.controller;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.DatePicker;

public class NewSaleController {

    @FXML private TextField customerNameField;
    @FXML private TextField itemNameField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private TableView<?> itemsTable;
    @FXML private DatePicker saleDate;

    @FXML
    private void handleAddItem() {
        // Add item to TableView (use ObservableList)
        System.out.println("Item added: " + itemNameField.getText());
    }

    @FXML
    private void handleSaveSale() {
        // Collect customer, item, payment info
        // Save to customers, sales_items, sales_invoices, payments_received
        System.out.println("Saving new sale...");
    }
}
