package org.nishgrid.clienterp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Launcher extends Application {

    private static final String FRONTEND_JAR_PATH = "frontend-app.jar";

    @Override
    public void start(Stage stage) throws Exception {

        launchFrontend();
    }

    private void launchFrontend() {
        try {
            File jar = new File(FRONTEND_JAR_PATH);
            if (!jar.exists()) {
                System.err.println("Frontend not found! Exiting Launcher.");
                return;
            }

            ProcessBuilder pb = new ProcessBuilder("java", "-jar", FRONTEND_JAR_PATH);
            pb.inheritIO();
            pb.start();
            Platform.exit();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to launch frontend!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}