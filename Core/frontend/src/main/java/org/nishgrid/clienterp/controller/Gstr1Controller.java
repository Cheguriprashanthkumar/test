package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.dto.Gstr1ItemDTO;
import org.nishgrid.clienterp.model.Gstr1Item;
import org.nishgrid.clienterp.service.ApiService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class Gstr1Controller {

    @FXML private TableView<Gstr1Item> gstr1Table;
    @FXML private TableColumn<Gstr1Item, Integer> invoiceIdCol;
    @FXML private TableColumn<Gstr1Item, String> customerGstinCol, invoiceDateCol, hsnCol, exportMonthCol;
    @FXML private TableColumn<Gstr1Item, Number> taxableValCol, gstRateCol, cgstCol, sgstCol, igstCol;
    @FXML private Button refreshButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_URL = ApiService.getBaseUrl()+"/gst/gstr1-items";

    @FXML
    public void initialize() {
        invoiceIdCol.setCellValueFactory(data -> data.getValue().invoiceIdProperty().asObject());
        customerGstinCol.setCellValueFactory(data -> data.getValue().customerGstinProperty());
        invoiceDateCol.setCellValueFactory(data -> data.getValue().invoiceDateProperty());
        hsnCol.setCellValueFactory(data -> data.getValue().itemHsnProperty());
        taxableValCol.setCellValueFactory(data -> data.getValue().taxableValueProperty());
        gstRateCol.setCellValueFactory(data -> data.getValue().gstRateProperty());
        cgstCol.setCellValueFactory(data -> data.getValue().cgstAmountProperty());
        sgstCol.setCellValueFactory(data -> data.getValue().sgstAmountProperty());
        igstCol.setCellValueFactory(data -> data.getValue().igstAmountProperty());
        exportMonthCol.setCellValueFactory(data -> data.getValue().exportMonthProperty());

        refreshButton.setOnAction(e -> loadGstr1Items());
        loadGstr1Items();
    }

    private void loadGstr1Items() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<Gstr1ItemDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
                        List<Gstr1Item> fxList = dtoList.stream()
                                .map(dto -> new Gstr1Item(
                                        dto.getInvoiceId().intValue(), dto.getCustomerGstin(), dto.getInvoiceDate().toString(),
                                        dto.getItemHsn(), dto.getTaxableValue(), dto.getGstRate(),
                                        dto.getCgstAmount(), dto.getSgstAmount(), dto.getIgstAmount(), dto.getExportMonth()
                                ))
                                .collect(Collectors.toList());
                        Platform.runLater(() -> gstr1Table.setItems(FXCollections.observableArrayList(fxList)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}