package org.nishgrid.clienterp;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.nishgrid.clienterp.util.ConfigManager;

import java.io.InputStream;

public class MainApp extends Application {

    private static HostServices hostServices;

    @Override
    public void start(Stage primaryStage) throws Exception {
        hostServices = getHostServices();


        InputStream faviconStream = getClass().getResourceAsStream("/favicon.png");
        if (faviconStream != null) {
            primaryStage.getIcons().add(new Image(faviconStream));
        } else {
            System.err.println("Favicon not found! Please place /favicon.png in resources.");
        }

        ConfigManager config = new ConfigManager();

        FXMLLoader loader;
        if (!config.isSetupCompleted()) {
            loader = new FXMLLoader(getClass().getResource("/fxml/license_key.fxml"));
            primaryStage.setTitle("Enter License Key");
        } else if (!config.isClientDetailsCompleted()) {
            loader = new FXMLLoader(getClass().getResource("/fxml/client_details.fxml"));
            primaryStage.setTitle("Client Form");
        } else {
            loader = new FXMLLoader(getClass().getResource("/fxml/login_form.fxml"));
            primaryStage.setTitle("Login");
        }

        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static HostServices getAppHostServices() {
        return hostServices;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
