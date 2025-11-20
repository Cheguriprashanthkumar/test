package org.nishgrid.clienterp.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Admin1Dashboard {
    public static void show(Stage stage) {
        try {
            Parent root = FXMLLoader.load(Admin1Dashboard.class.getResource("/fxml/Admin1Dashboard.fxml"));
            Scene scene = new Scene(root, 1000, 600);
            stage.setScene(scene);
            stage.setTitle("Admin1 Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
