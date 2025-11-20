package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.dto.SmsLogDTO;
import org.nishgrid.clienterp.model.SmsWhatsappLog;
import org.nishgrid.clienterp.service.ApiService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SmsWhatsappLogController {

    @FXML private TableView<SmsWhatsappLog> smsLogsTable;
    @FXML private TableColumn<SmsWhatsappLog, Integer> messageIdCol, customerIdCol;
    @FXML private TableColumn<SmsWhatsappLog, String> messageTypeCol, statusCol, timestampCol;
    @FXML private Button refreshButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_URL = ApiService.getBaseUrl()+"/sms-logs";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        messageIdCol.setCellValueFactory(data -> data.getValue().messageIdProperty().asObject());
        customerIdCol.setCellValueFactory(data -> data.getValue().customerIdProperty().asObject());
        messageTypeCol.setCellValueFactory(data -> data.getValue().messageTypeProperty());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        timestampCol.setCellValueFactory(data -> data.getValue().timestampProperty());

        refreshButton.setOnAction(e -> loadLogs());
        loadLogs();
    }

    private void loadLogs() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<SmsLogDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
                        List<SmsWhatsappLog> fxList = dtoList.stream().map(this::dtoToFx).collect(Collectors.toList());
                        Platform.runLater(() -> smsLogsTable.setItems(FXCollections.observableArrayList(fxList)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private SmsWhatsappLog dtoToFx(SmsLogDTO dto) {
        return new SmsWhatsappLog(
                dto.getMessageId().intValue(),
                dto.getCustomerId().intValue(),
                dto.getMessageType(),
                dto.getStatus(),
                dto.getTimestamp() != null ? dto.getTimestamp().format(formatter) : ""
        );
    }
}