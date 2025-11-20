package org.nishgrid.clienterp.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.nishgrid.clienterp.service.ApiService;
import org.nishgrid.clienterp.util.ConfigManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class HomeController {

    @FXML private ImageView clientImageView;

    private String uniqueId;

    @FXML
    public void initialize() {
        this.uniqueId = ConfigManager.getInstance().getUniqueId();
        if (this.uniqueId == null || this.uniqueId.isBlank()) {
            System.err.println("HomeController: Could not load uniqueId from ConfigManager.");
            return;
        }
        loadClientPhotoFromAPI();
    }

    @FXML
    private void handleChangePhoto() {
        if (this.uniqueId == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Window stage = clientImageView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            new Thread(() -> {
                savePhotoToAPI(file);
                Platform.runLater(() -> {
                    try {
                        clientImageView.setImage(new Image(new FileInputStream(file)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }).start();
        }
    }

    private void savePhotoToAPI(File file) {
        String apiUrl = ApiService.getBaseUrl() + "/photo/" + this.uniqueId;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                Files.copy(file.toPath(), os);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Photo uploaded successfully.");
            } else {
                System.err.println("Failed to upload photo. Response code: " + responseCode);
            }
            conn.disconnect();
        } catch (IOException e) {
            System.err.println("API error while saving photo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadClientPhotoFromAPI() {
        String apiUrl = ApiService.getBaseUrl() + "/photo/" + this.uniqueId;

        new Thread(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    Image image = new Image(is);
                    Platform.runLater(() -> clientImageView.setImage(image));
                    is.close();
                } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    System.out.println("No photo found on server for unique ID: " + this.uniqueId);
                } else {
                    System.err.println("Failed to load photo. Response code: " + responseCode);
                }
                conn.disconnect();
            } catch (IOException e) {
                System.err.println("API error while loading photo: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
