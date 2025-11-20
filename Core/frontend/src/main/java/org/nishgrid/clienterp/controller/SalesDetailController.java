package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.nishgrid.clienterp.dto.SalesInvoiceDto;
import org.nishgrid.clienterp.dto.SalesItemDto;
import org.nishgrid.clienterp.dto.SalesReturnTotalAmountDto;
import org.nishgrid.clienterp.service.ApiService;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SalesDetailController {

    @FXML private Label invoiceNoLabel, invoiceDateLabel, customerNameLabel, customerMobileLabel;
    @FXML private TableView<SalesItemDto> itemsTable;
    @FXML private TableColumn<SalesItemDto, String> itemNameCol;
    @FXML private TableColumn<SalesItemDto, Double> netWeightCol;
    @FXML private TableColumn<SalesItemDto, BigDecimal> totalPriceCol;
    @FXML private Label totalAmountLabel, returnedAmountLabel, finalAmountLabel;
    @FXML private Label oldGoldLabel, paidAmountLabel, dueAmountLabel;

    private final BigDecimal ZERO = BigDecimal.ZERO;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public void loadInvoiceData(Long invoiceId) {
        String invoiceUrl = ApiService.getBaseUrl() + "/sales/" + invoiceId;
        String returnedAmountUrl = ApiService.getBaseUrl() + "/returns/total-amount-by-invoice/" + invoiceId;

        HttpRequest invoiceRequest = HttpRequest.newBuilder().uri(URI.create(invoiceUrl)).GET().build();
        HttpRequest returnedAmountRequest = HttpRequest.newBuilder().uri(URI.create(returnedAmountUrl)).GET().build();

        CompletableFuture<HttpResponse<String>> invoiceFuture = httpClient.sendAsync(invoiceRequest, HttpResponse.BodyHandlers.ofString());
        CompletableFuture<HttpResponse<String>> returnedAmountFuture = httpClient.sendAsync(returnedAmountRequest, HttpResponse.BodyHandlers.ofString());

        CompletableFuture.allOf(invoiceFuture, returnedAmountFuture)
                .thenAccept(v -> {
                    try {
                        HttpResponse<String> invoiceResponse = invoiceFuture.join();
                        HttpResponse<String> returnedAmountResponse = returnedAmountFuture.join();

                        if (invoiceResponse.statusCode() == 200 && returnedAmountResponse.statusCode() == 200) {
                            SalesInvoiceDto invoice = objectMapper.readValue(invoiceResponse.body(), SalesInvoiceDto.class);
                            SalesReturnTotalAmountDto responseDto = objectMapper.readValue(returnedAmountResponse.body(), SalesReturnTotalAmountDto.class);
                            BigDecimal totalReturnedAmount = Optional.ofNullable(responseDto.getTotalReturnedAmount()).orElse(ZERO);

                            Platform.runLater(() -> populateView(invoice, totalReturnedAmount));
                        } else {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Failed to load invoice details");
                                alert.setContentText("The server returned a status code of " + invoiceResponse.statusCode());
                                alert.showAndWait();
                            });
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Connection Error");
                            alert.setHeaderText("Failed to connect to the server");
                            alert.setContentText("An error occurred while fetching data: " + e.getMessage());
                            alert.showAndWait();
                        });
                    }
                });
    }

    private void populateView(SalesInvoiceDto invoice, BigDecimal totalReturnedAmount) {
        invoiceNoLabel.setText(Optional.ofNullable(invoice.getInvoiceNo()).orElse("-"));
        invoiceDateLabel.setText(Optional.ofNullable(invoice.getInvoiceDate()).map(LocalDate::toString).orElse("-"));
        if (invoice.getCustomer() != null) {
            customerNameLabel.setText(Optional.ofNullable(invoice.getCustomer().getName()).orElse("-"));
            customerMobileLabel.setText(Optional.ofNullable(invoice.getCustomer().getMobile()).orElse("-"));
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        BigDecimal netAmount = Optional.ofNullable(invoice.getNetAmount()).orElse(ZERO);
        BigDecimal oldGoldValue = Optional.ofNullable(invoice.getOldGoldValue()).orElse(ZERO);
        BigDecimal paidAmount = Optional.ofNullable(invoice.getPaidAmount()).orElse(ZERO);

        BigDecimal currentFinalAmount;
        BigDecimal finalPayableAmount;

        // New Logic: Deduct returned amount first, then apply old gold credit.
        // This preserves the non-refundable components included in netAmount.
        currentFinalAmount = netAmount.subtract(totalReturnedAmount).subtract(oldGoldValue);
        finalPayableAmount = currentFinalAmount;

        if (currentFinalAmount.compareTo(ZERO) < 0) currentFinalAmount = ZERO;

        BigDecimal dueAmount = finalPayableAmount.subtract(paidAmount);
        if (dueAmount.compareTo(ZERO) < 0) dueAmount = ZERO;

        totalAmountLabel.setText(currencyFormat.format(netAmount));
        oldGoldLabel.setText(currencyFormat.format(oldGoldValue));
        returnedAmountLabel.setText(currencyFormat.format(totalReturnedAmount));
        paidAmountLabel.setText(currencyFormat.format(paidAmount));
        finalAmountLabel.setText(currencyFormat.format(currentFinalAmount));
        dueAmountLabel.setText(currencyFormat.format(dueAmount));

        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        netWeightCol.setCellValueFactory(new PropertyValueFactory<>("netWeight"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : currencyFormat.format(item));
            }
        });

        itemsTable.setItems(FXCollections.observableArrayList(invoice.getSalesItems()));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) invoiceNoLabel.getScene().getWindow();
        stage.close();
    }
}