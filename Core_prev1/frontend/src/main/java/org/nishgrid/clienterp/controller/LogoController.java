package org.nishgrid.clienterp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class LogoController {

    @FXML
    private Label logoPathLabel;

    @FXML
    private ImageView logoPreview;

    private File selectedLogoFile;

    @FXML
    private void handleChooseLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Logo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            selectedLogoFile = file;

            System.out.println("Selected file name: " + file.getName());
            System.out.println("Selected full path: " + file.getAbsolutePath());

            logoPathLabel.setText(file.getName());

            Image image = new Image(file.toURI().toString());
            logoPreview.setImage(image);
        }
    }

    public File getSelectedLogoFile() {
        return selectedLogoFile;
    }
}
