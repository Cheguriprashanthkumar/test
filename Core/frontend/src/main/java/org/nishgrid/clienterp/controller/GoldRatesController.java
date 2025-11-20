package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.nishgrid.clienterp.dto.GoldRateDTO;
import org.nishgrid.clienterp.model.GoldRate; // Your JavaFX Model
import org.nishgrid.clienterp.service.ApiService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GoldRatesController {

    // --- FXML Fields ---
    @FXML private TableView<GoldRate> goldRatesTable;
    @FXML private TableColumn<GoldRate, LocalDateTime> dateCol;
    @FXML private TableColumn<GoldRate, String> rate24kCol;
    @FXML private TableColumn<GoldRate, String> rate22kCol;
    @FXML private TableColumn<GoldRate, String> rate18kCol;
    @FXML private TableColumn<GoldRate, String> rate14kCol;
    @FXML private TableColumn<GoldRate, String> rate12kCol;
    @FXML private TableColumn<GoldRate, String> rate10kCol;
    @FXML private TableColumn<GoldRate, String> rate09kCol;
    @FXML private TableColumn<GoldRate, String> fineSilverCol;
    @FXML private TableColumn<GoldRate, String> sterlingSilverCol;
    @FXML private TableColumn<GoldRate, String> coinSilverCol;
    @FXML private Button refreshButton;

    // --- Class members ---
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String API_URL = ApiService.getBaseUrl()+"/gold-rates/latest";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        // Set up cell value factories
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        rate24kCol.setCellValueFactory(new PropertyValueFactory<>("rate24k"));
        rate22kCol.setCellValueFactory(new PropertyValueFactory<>("rate22k"));
        rate18kCol.setCellValueFactory(new PropertyValueFactory<>("rate18k"));
        rate14kCol.setCellValueFactory(new PropertyValueFactory<>("rate14k"));
        rate12kCol.setCellValueFactory(new PropertyValueFactory<>("rate12k"));
        rate10kCol.setCellValueFactory(new PropertyValueFactory<>("rate10k"));
        rate09kCol.setCellValueFactory(new PropertyValueFactory<>("rate09k"));
        fineSilverCol.setCellValueFactory(new PropertyValueFactory<>("fineSilver"));
        sterlingSilverCol.setCellValueFactory(new PropertyValueFactory<>("sterlingSilver"));
        coinSilverCol.setCellValueFactory(new PropertyValueFactory<>("coinSilver"));

        // Set custom CellFactory for date formatting
        dateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // THIS IS THE FIX: Programmatically set the button's action
        if (refreshButton != null) {
            refreshButton.setOnAction(event -> loadLatestRates());
        }

        // Load data on initial view
        loadLatestRates();
    }

    // This is now a regular private method, NOT linked to FXML directly
    private void loadLatestRates() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        // Deserialize JSON into the DTO
                        GoldRateDTO dto = objectMapper.readValue(body, GoldRateDTO.class);

                        // Map the DTO to your JavaFX-friendly model
                        GoldRate fxModel = new GoldRate(
                                dto.getDate(),
                                String.format("%.2f", dto.getRate24k()),
                                String.format("%.2f", dto.getRate22k()),
                                String.format("%.2f", dto.getRate18k()),
                                String.format("%.2f", dto.getRate14k()),
                                String.format("%.2f", dto.getRate12k()),
                                String.format("%.2f", dto.getRate10k()),
                                String.format("%.2f", dto.getRate09k()),
                                String.format("%.2f", dto.getFineSilver()),
                                String.format("%.2f", dto.getSterlingSilver()),
                                String.format("%.2f", dto.getCoinSilver())
                        );

                        // Update the UI on the JavaFX Application Thread
                        Platform.runLater(() -> {
                            goldRatesTable.setItems(FXCollections.observableArrayList(fxModel));
                        });

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                });
    }
}