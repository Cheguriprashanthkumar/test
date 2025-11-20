package org.nishgrid.clienterp.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class HandleChooseLogo {

    /**
     * Lets user choose an image, saves it to 'uploads', shows preview, and returns the saved path.
     *
     * @param ownerWindow   the owner window for the FileChooser
     * @param logoPathLabel label to display path
     * @param logoPreview   image view to show preview
     * @return relative path of saved image or null
     */
    public static String chooseAndSaveLogo(Window ownerWindow, Label logoPathLabel, ImageView logoPreview) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Logo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(ownerWindow);
        if (file != null) {
            try {
                // Create uploads folder if not exists
                Path uploadsDir = Paths.get("uploads");
                Files.createDirectories(uploadsDir);

                String fileName = System.currentTimeMillis() + "_" + file.getName();
                Path dest = uploadsDir.resolve(fileName);
                Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

                String savedPath = dest.toString();

                // ✅ Show path & preview
                logoPathLabel.setText(savedPath);
                logoPreview.setImage(new Image(dest.toUri().toString()));

                return savedPath;

            } catch (IOException e) {
                e.printStackTrace();
                logoPathLabel.setText("Error saving file: " + e.getMessage());
            }
        }
        return null;
    }
}
