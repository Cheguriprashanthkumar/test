package org.nishgrid.clienterp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.nishgrid.clienterp.model.EmployeeFx;

import java.time.format.DateTimeFormatter;

public class ViewEmployeeController {

    @FXML private ImageView employeePhoto;
    @FXML private Label nameLabel, emailLabel, phoneLabel, departmentLabel, designationLabel,
            dobLabel, dojLabel, genderLabel, addressLabel, aadhaarLabel, panLabel, statusLabel;
    @FXML private VBox photoContainer;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy");

    public void setEmployee(EmployeeFx employee) {
        if (employee == null) return;

        // Set basic info
        nameLabel.setText(employee.getFullName());
        emailLabel.setText(employee.getEmail());
        phoneLabel.setText(String.valueOf(employee.getMobileNumber1()));
        departmentLabel.setText(employee.getDepartment());
        designationLabel.setText(employee.getDesignation());
        genderLabel.setText(employee.getGender());
        addressLabel.setText(employee.getAddress());
        aadhaarLabel.setText(String.valueOf(employee.getAadhaarNumber()));
        panLabel.setText(employee.getPanCardNumber());
        statusLabel.setText(employee.getStatus());

        // Format dates for better readability
        if (employee.getDob() != null) {
            dobLabel.setText(employee.getDob().format(formatter));
        }
        if (employee.getDoj() != null) {
            dojLabel.setText(employee.getDoj().format(formatter));
        }

        // Load employee photo
        try {
            // Assuming the 'photo' field contains a URL or a file path
            // For this example, we'll try to load it as a URL.
            // A placeholder is used if it fails.
            Image image = new Image(employee.getPhoto(), true); // true = load in background
            employeePhoto.setImage(image);
        } catch (Exception e) {
            // Set a placeholder image if the provided path is invalid or null
            Image placeholder = new Image(getClass().getResourceAsStream("/icons/placeholder.png"));
            employeePhoto.setImage(placeholder);
            System.err.println("Could not load employee photo from: " + employee.getPhoto());
        }
    }
}
