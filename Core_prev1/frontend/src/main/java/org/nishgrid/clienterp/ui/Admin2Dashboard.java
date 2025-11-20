package org.nishgrid.clienterp.ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Admin2Dashboard {
    public static void show(Stage stage) {
        Label label = new Label("Welcome to Admin2 Dashboard");
        Scene scene = new Scene(new StackPane(label), 600, 400);
        stage.setScene(scene);
        stage.setTitle("Admin2 Dashboard");
        stage.show();
    }
}

