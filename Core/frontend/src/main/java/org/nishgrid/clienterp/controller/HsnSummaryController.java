package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.nishgrid.clienterp.dto.HsnSummaryDTO;
import org.nishgrid.clienterp.model.HsnSummary;
import org.nishgrid.clienterp.service.ApiService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HsnSummaryController {

    @FXML private TableView<HsnSummary> hsnTable;
    @FXML private TableColumn<HsnSummary, String> hsnCodeCol;
    @FXML private TableColumn<HsnSummary, Number> totalQtyCol;
    @FXML private TableColumn<HsnSummary, Number> taxableValueCol;
    @FXML private TableColumn<HsnSummary, Number> gstAmountCol;
    @FXML private TableColumn<HsnSummary, String> monthCol;
    @FXML private ComboBox<String> monthFilterComboBox;
    @FXML private Button refreshButton;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private Button generateButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String API_BASE_URL = ApiService.getBaseUrl() + "/hsn-summary";
    private final ObservableList<HsnSummary> allSummaries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupInputControls();
        monthFilterComboBox.setOnAction(event -> filterTable());
        loadSummaries();
    }

    private void setupTableColumns() {
        hsnCodeCol.setCellValueFactory(data -> data.getValue().hsnCodeProperty());
        totalQtyCol.setCellValueFactory(data -> data.getValue().totalQtyProperty());
        taxableValueCol.setCellValueFactory(data -> data.getValue().taxableValueProperty());
        gstAmountCol.setCellValueFactory(data -> data.getValue().gstAmountProperty());
        monthCol.setCellValueFactory(data -> data.getValue().monthProperty());
    }

    private void setupInputControls() {
        int currentYear = Year.now().getValue();
        yearComboBox.setItems(FXCollections.observableArrayList(
                IntStream.rangeClosed(currentYear - 5, currentYear + 5).boxed().collect(Collectors.toList())
        ));
        monthComboBox.setItems(FXCollections.observableArrayList(
                IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList())
        ));
    }

    @FXML
    private void handleRefresh() {
        loadSummaries();
    }

    @FXML
    private void handleGenerateSummary() {
        Integer year = yearComboBox.getValue();
        Integer month = monthComboBox.getValue();

        if (year == null || month == null) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please select both a year and a month to generate the summary.");
            return;
        }

        String url = API_BASE_URL + "/generate?year=" + year + "&month=" + month;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "HSN summary generated successfully.");
                            loadSummaries();
                        });
                    } else {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate summary: " + response.body()));
                    }
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the server: " + e.getMessage()));
                    return null;
                });
    }

    private void loadSummaries() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::processApiResponse)
                .exceptionally(e -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to load summaries: " + e.getMessage()));
                    return null;
                });
    }

    private void processApiResponse(String body) {
        try {
            List<HsnSummaryDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
            List<HsnSummary> fxList = dtoList.stream()
                    .map(dto -> new HsnSummary(
                            dto.getHsnCode(),
                            dto.getTotalQty() != null ? dto.getTotalQty() : 0.0,
                            dto.getTaxableValue() != null ? dto.getTaxableValue() : 0.0,
                            dto.getGstAmount() != null ? dto.getGstAmount() : 0.0,
                            dto.getMonth()
                    ))
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                allSummaries.setAll(fxList);
                populateMonthFilter();
                filterTable();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateMonthFilter() {
        String selected = monthFilterComboBox.getValue();
        List<String> months = allSummaries.stream()
                .map(summary -> summary.monthProperty().get())
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        monthFilterComboBox.setItems(FXCollections.observableArrayList(months));

        if (months.contains(selected)) {
            monthFilterComboBox.setValue(selected);
        } else {
            monthFilterComboBox.getSelectionModel().clearSelection();
        }
    }

    private void filterTable() {
        String selectedMonth = monthFilterComboBox.getValue();
        if (selectedMonth == null || selectedMonth.isEmpty()) {
            hsnTable.setItems(allSummaries);
        } else {
            ObservableList<HsnSummary> filteredList = allSummaries.stream()
                    .filter(summary -> selectedMonth.equals(summary.monthProperty().get()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            hsnTable.setItems(filteredList);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

