package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.dto.Gstr3bSummaryDTO;
import org.nishgrid.clienterp.model.Gstr3bSummary;
import org.nishgrid.clienterp.service.ApiService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Gstr3bController {

    @FXML private TableView<Gstr3bSummary> gstr3bTable;
    @FXML private TableColumn<Gstr3bSummary, String> monthCol, statusCol, filedDateCol;
    @FXML private TableColumn<Gstr3bSummary, Number> outwardCol, inwardCol, igstCol, cgstCol, sgstCol;
    @FXML private Button refreshButton;
    @FXML private TextField monthYearField;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_URL = ApiService.getBaseUrl()+"/gst/gstr3b-summaries";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        monthCol.setCellValueFactory(data -> data.getValue().monthYearProperty());
        outwardCol.setCellValueFactory(data -> data.getValue().outwardSuppliesProperty());
        inwardCol.setCellValueFactory(data -> data.getValue().inwardSuppliesProperty());
        igstCol.setCellValueFactory(data -> data.getValue().igstProperty());
        cgstCol.setCellValueFactory(data -> data.getValue().cgstProperty());
        sgstCol.setCellValueFactory(data -> data.getValue().sgstProperty());
        statusCol.setCellValueFactory(data -> data.getValue().filedStatusProperty());
        filedDateCol.setCellValueFactory(data -> data.getValue().filedDateProperty());

        // Set a default value for the generation field
        monthYearField.setText(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));

        if (refreshButton != null) {
            refreshButton.setOnAction(e -> loadSummaries());
        }
        loadSummaries();
    }

    private void loadSummaries() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<Gstr3bSummaryDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
                        List<Gstr3bSummary> fxList = dtoList.stream()
                                .map(this::dtoToFx)
                                .collect(Collectors.toList());
                        Platform.runLater(() -> gstr3bTable.setItems(FXCollections.observableArrayList(fxList)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @FXML
    private void handleGenerateSummary() {
        String monthYear = monthYearField.getText().trim();
        if (monthYear.isEmpty() || !monthYear.matches("\\d{4}-\\d{2}")) {
            showAlert("Invalid Format", "Please enter the month in YYYY-MM format.");
            return;
        }

        String url = API_URL + "/generate?monthYear=" + monthYear;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            showAlert("Success", "GSTR-3B summary for " + monthYear + " generated successfully!");
                            loadSummaries(); // Refresh the table
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Failed to generate summary. Response: " + response.body()));
                    }
                });
    }

    private Gstr3bSummary dtoToFx(Gstr3bSummaryDTO dto) {
        return new Gstr3bSummary(
                dto.getMonthYear(), dto.getOutwardTaxableSupplies(),
                dto.getInwardSupplies(), dto.getIgst(), dto.getCgst(), dto.getSgst(),
                dto.getFiledStatus(), dto.getFiledDate() != null ? dto.getFiledDate().format(formatter) : ""
        );
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}