package org.nishgrid.clienterp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class ViewEditController {

    @FXML private StackPane contentPane;
    @FXML private Button btnEmployee, btnAttendance, btnSalary, btnLeaves, btnExit;

    private void updateTabStyle(Button selectedButton) {
        btnEmployee.getStyleClass().remove("selected");
        btnAttendance.getStyleClass().remove("selected");
        btnSalary.getStyleClass().remove("selected");
        btnLeaves.getStyleClass().remove("selected");
        btnExit.getStyleClass().remove("selected");

        selectedButton.getStyleClass().add("selected");
    }

    public void showEmployeeTab() {
        updateTabStyle(btnEmployee);
        loadFXMLIntoContentPane("/fxml/employee-list.fxml");
    }

    public void showAttendanceTab() {
        updateTabStyle(btnAttendance);
        loadFXMLIntoContentPane("/fxml/attendance_tab.fxml");
    }

    public void showSalaryTab() {
        updateTabStyle(btnSalary);
        loadFXMLIntoContentPane("/fxml/salary_tab.fxml");
    }

    public void showLeavesTab() {
        updateTabStyle(btnLeaves);
        loadFXMLIntoContentPane("/fxml/leave_requests.fxml");
    }

    public void showExitTab() {
        updateTabStyle(btnExit);
        loadFXMLIntoContentPane("/fxml/exit_table.fxml");
    }

    private void loadFXMLIntoContentPane(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
