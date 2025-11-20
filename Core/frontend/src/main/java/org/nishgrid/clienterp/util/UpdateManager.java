package org.nishgrid.clienterp.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.Optional;
import java.util.Properties;


public class UpdateManager {

    private static final String SERVER_VERSION_URL = "https://client-nishgrid.co.in/ngst/updates/version.json";
    private static final String LOCAL_CONFIG_DIR = "config";
    private static final String LOCAL_VERSION_FILE = LOCAL_CONFIG_DIR + "/update.properties";
    private static final String FRONTEND_JAR_PATH = "frontend-app.jar";


    public static void checkForUpdates(Runnable onUpdated) {
        new Thread(() -> {
            String latestVersion = "0.0.0";
            String jarUrl = "";
            String releaseNotes = "";
            File versionFile = null;
            Properties localProps = new Properties();

            try {

                File configDir = new File(LOCAL_CONFIG_DIR);
                if (!configDir.exists()) configDir.mkdirs();

                versionFile = new File(LOCAL_VERSION_FILE);
                if (versionFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(versionFile)) {
                        localProps.load(fis);
                    }
                }
                String currentVersion = localProps.getProperty("version", "0.0.0");


                String jsonText = new String(new URL(SERVER_VERSION_URL).openStream().readAllBytes()).trim();


                JSONObject json = new JSONObject(jsonText);
                latestVersion = json.optString("version", "0.0.0");
                jarUrl = json.optString("jar_url", "");
                releaseNotes = json.optString("release_notes", "No release notes available.");


                if (!latestVersion.equals(currentVersion)) {

                    showUpdateDialog(latestVersion, jarUrl, releaseNotes, versionFile, localProps, onUpdated);
                } else {

                    String finalCurrentVersion = currentVersion;
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("No Updates Found");
                        alert.setHeaderText("You are already using the latest version: " + finalCurrentVersion);
                        alert.showAndWait();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Failed to check for updates.\n" + e.getMessage(), ButtonType.OK);
                    alert.show();
                });
            }
        }).start();
    }


    private static void showUpdateDialog(String latestVersion, String jarUrl, String releaseNotes,
                                         File versionFile, Properties localProps, Runnable onUpdated) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Update Available");
            alert.setHeaderText("New version " + latestVersion + " available");
            // Display release notes directly in the dialog content
            alert.setContentText("Release Notes:\n" + releaseNotes + "\n\nUpdate now?");

            ButtonType updateBtn = new ButtonType("Update Now");
            ButtonType cancelBtn = new ButtonType("Later");
            alert.getButtonTypes().setAll(updateBtn, cancelBtn);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == updateBtn) {

                performUpdate(latestVersion, jarUrl, versionFile, localProps, onUpdated);
            }
        });
    }


    private static void performUpdate(String latestVersion, String jarUrl, File versionFile,
                                      Properties localProps, Runnable onUpdated) {
        new Thread(() -> {
            try {

                try (InputStream in = new URL(jarUrl).openStream()) {

                    Files.copy(in, Paths.get(FRONTEND_JAR_PATH), StandardCopyOption.REPLACE_EXISTING);
                }


                localProps.setProperty("version", latestVersion);


                try (FileOutputStream fos = new FileOutputStream(versionFile)) {
                    localProps.store(fos, "Frontend version info");
                }


                Platform.runLater(() -> {
                    Alert success = new Alert(Alert.AlertType.INFORMATION, "Updated to version " + latestVersion + ". Please restart the application.");
                    success.showAndWait();
                    if (onUpdated != null) onUpdated.run();
                });
            } catch (IOException e) {
                e.printStackTrace();

                Platform.runLater(() -> {
                    Alert fail = new Alert(Alert.AlertType.ERROR, "Update failed: " + e.getMessage(), ButtonType.OK);
                    fail.show();
                });
            }
        }).start();
    }
}