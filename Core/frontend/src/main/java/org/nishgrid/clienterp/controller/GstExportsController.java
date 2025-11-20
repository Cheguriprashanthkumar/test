package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.nishgrid.clienterp.dto.GstExportDTO;
import org.nishgrid.clienterp.model.GstExport;
import org.nishgrid.clienterp.service.ApiService;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class GstExportsController {

    @FXML private TextField monthYearField;
    @FXML private TableView<GstExport> exportsTable;
    @FXML private TableColumn<GstExport, Integer> exportIdCol;
    @FXML private TableColumn<GstExport, String> monthYearCol, generatedOnCol, filePathCol, statusCol, submittedOnCol;
    @FXML private TableColumn<GstExport, Void> actionsCol;
    @FXML private Button refreshButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_BASE_URL = ApiService.getBaseUrl()+"/gst/exports";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        // Completed the setup for all table columns
        exportIdCol.setCellValueFactory(data -> data.getValue().gstExportIdProperty().asObject());
        monthYearCol.setCellValueFactory(data -> data.getValue().monthYearProperty());
        generatedOnCol.setCellValueFactory(data -> data.getValue().generatedOnProperty());
        filePathCol.setCellValueFactory(data -> data.getValue().filePathProperty());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        submittedOnCol.setCellValueFactory(data -> data.getValue().submittedOnProperty());

        monthYearField.setText(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        refreshButton.setOnAction(e -> loadExports());
        actionsCol.setCellFactory(createActionsColumn());
        loadExports();
    }

    private void loadExports() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<GstExportDTO> dtoList = objectMapper.readValue(body, new TypeReference<>() {});
                        List<GstExport> fxList = dtoList.stream().map(this::dtoToFx).collect(Collectors.toList());
                        Platform.runLater(() -> exportsTable.setItems(FXCollections.observableArrayList(fxList)));
                    } catch (Exception e) { e.printStackTrace(); }
                });
    }

    @FXML
    private void handleGenerateExport() {
        String monthYear = monthYearField.getText().trim();
        if (monthYear.isEmpty() || !monthYear.matches("\\d{4}-\\d{2}")) {
            showAlert("Error", "Please enter the month in YYYY-MM format.");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/generate?monthYear=" + monthYear))
                .POST(HttpRequest.BodyPublishers.noBody()).build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            showAlert("Success", "Export file generated successfully!");
                            loadExports();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Failed to generate file: " + response.body()));
                    }
                });
    }

    private void handleDownload(GstExport export) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/" + export.getGstExportId() + "/download"))
                .GET().build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> saveCsvFile(response.body(), export.getMonthYear()));
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Failed to download file."));
                    }
                });
    }

    private void saveCsvFile(byte[] data, String monthYear) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save GST Export");
        fileChooser.setInitialFileName("gstr1_export_" + monthYear.replace("-", "") + ".csv");
        File file = fileChooser.showSaveDialog(exportsTable.getScene().getWindow());
        if (file != null) {
            try {
                Files.write(file.toPath(), data);
                showAlert("Success", "File saved successfully!");
            } catch (Exception e) {
                showAlert("Error", "Could not save file.");
                e.printStackTrace();
            }
        }
    }

    private Callback<TableColumn<GstExport, Void>, TableCell<GstExport, Void>> createActionsColumn() {
        return param -> new TableCell<>() {
            private final Button downloadBtn = new Button("Download");
            {
                downloadBtn.setOnAction(e -> {
                    GstExport item = getTableRow().getItem();
                    if (item != null) {
                        handleDownload(item);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : (getTableRow() != null && getTableRow().getItem() != null ? downloadBtn : null));
            }
        };
    }

    // Implemented the missing dtoToFx helper method
    private GstExport dtoToFx(GstExportDTO dto) {
        return new GstExport(
                dto.getGstExportId().intValue(),
                dto.getMonthYear(),
                dto.getGeneratedOn() != null ? dto.getGeneratedOn().format(formatter) : "",
                dto.getFilePath(),
                dto.getStatus(),
                dto.getSubmittedOn() != null ? dto.getSubmittedOn().format(formatter) : ""
        );
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equalsIgnoreCase("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}