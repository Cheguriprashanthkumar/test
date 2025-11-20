package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.dto.SalesAuditLogDTO; // Client-side DTO
import org.nishgrid.clienterp.model.SalesAuditLog;  // JavaFX Model
import org.nishgrid.clienterp.service.ApiService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SalesAuditLogController {

    @FXML private TableView<SalesAuditLog> auditTable;
    @FXML private TableColumn<SalesAuditLog, Integer> logIdCol;
    @FXML private TableColumn<SalesAuditLog, Integer> invoiceIdCol;
    @FXML private TableColumn<SalesAuditLog, String> actionCol;
    @FXML private TableColumn<SalesAuditLog, String> performedByCol;
    @FXML private TableColumn<SalesAuditLog, String> timestampCol;
    @FXML private TableColumn<SalesAuditLog, String> oldDataCol;
    @FXML private TableColumn<SalesAuditLog, String> newDataCol;
    @FXML private Button refreshButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_URL = ApiService.getBaseUrl()+"/audit-logs";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        logIdCol.setCellValueFactory(data -> data.getValue().logIdProperty().asObject());
        invoiceIdCol.setCellValueFactory(data -> data.getValue().invoiceIdProperty().asObject());
        actionCol.setCellValueFactory(data -> data.getValue().actionProperty());
        performedByCol.setCellValueFactory(data -> data.getValue().performedByProperty());
        timestampCol.setCellValueFactory(data -> data.getValue().timestampProperty());
        oldDataCol.setCellValueFactory(data -> data.getValue().oldDataProperty());
        newDataCol.setCellValueFactory(data -> data.getValue().newDataProperty());

        refreshButton.setOnAction(event -> loadAuditLogs());
        loadAuditLogs();
    }


    private void loadAuditLogs() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {

                    System.out.println("--- RAW JSON FROM SERVER ---");
                    System.out.println(body);


                    try {
                        List<SalesAuditLogDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});

                        List<SalesAuditLog> fxList = dtoList.stream()
                                .map(dto -> new SalesAuditLog(
                                        dto.getLogId().intValue(),
                                        dto.getInvoiceId() != null ? dto.getInvoiceId().intValue() : 0,
                                        dto.getAction(),
                                        dto.getPerformedBy(),
                                        dto.getTimestamp().format(formatter),
                                        dto.getOldData(),
                                        dto.getNewData()
                                ))
                                .collect(Collectors.toList());

                        Platform.runLater(() -> auditTable.setItems(FXCollections.observableArrayList(fxList)));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}