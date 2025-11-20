package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.dto.EmailLogDTO;
import org.nishgrid.clienterp.model.EmailLog;
import org.nishgrid.clienterp.service.ApiService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EmailLogController {

    @FXML private TableView<EmailLog> emailLogsTable;
    @FXML private TableColumn<EmailLog, Integer> emailIdCol, invoiceIdCol, customerIdCol;
    @FXML private TableColumn<EmailLog, String> emailAddressCol, statusCol, sentTimeCol, errorCol;
    @FXML private Button refreshButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_URL = ApiService.getBaseUrl()+"/email-logs";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        emailIdCol.setCellValueFactory(data -> data.getValue().emailIdProperty().asObject());
        invoiceIdCol.setCellValueFactory(data -> data.getValue().invoiceIdProperty().asObject());
        customerIdCol.setCellValueFactory(data -> data.getValue().customerIdProperty().asObject());
        emailAddressCol.setCellValueFactory(data -> data.getValue().emailAddressProperty());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        sentTimeCol.setCellValueFactory(data -> data.getValue().sentTimeProperty());
        errorCol.setCellValueFactory(data -> data.getValue().errorMessageProperty());

        refreshButton.setOnAction(e -> loadLogs());
        loadLogs();
    }

    private void loadLogs() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<EmailLogDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
                        List<EmailLog> fxList = dtoList.stream().map(this::dtoToFx).collect(Collectors.toList());
                        Platform.runLater(() -> emailLogsTable.setItems(FXCollections.observableArrayList(fxList)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private EmailLog dtoToFx(EmailLogDTO dto) {
        return new EmailLog(
                dto.getEmailId() != null ? dto.getEmailId().intValue() : 0,
                dto.getInvoiceId() != null ? dto.getInvoiceId().intValue() : 0,
                dto.getCustomerId() != null ? dto.getCustomerId().intValue() : 0,
                dto.getEmailAddress(),
                dto.getStatus(),
                dto.getSentTime() != null ? dto.getSentTime().format(formatter) : "",
                dto.getErrorMessage()
        );
    }
}
