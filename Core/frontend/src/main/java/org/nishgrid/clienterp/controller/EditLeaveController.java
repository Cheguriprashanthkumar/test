package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.LeaveRequest;
import org.nishgrid.clienterp.service.ApiService;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditLeaveController {

    @FXML private TextField txtLeaveId;
    @FXML private TextField txtEmpCode;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button btnSave;

    private LeaveRequest currentLeave;

    public void initialize() {
        statusCombo.getItems().addAll("Pending", "Approved", "Rejected");
        btnSave.setOnAction(e -> saveStatusUpdate());
    }

    public void setLeaveRequest(LeaveRequest leave) {
        this.currentLeave = leave;

        txtLeaveId.setText(String.valueOf(leave.getLeaveId()));
        txtEmpCode.setText(leave.getEmployeeCode());
        statusCombo.setValue(leave.getStatus());
    }
    private void saveStatusUpdate() {
        String newStatus = statusCombo.getValue();
        int id = currentLeave.getLeaveId();

        try {
            // Construct the URL with query parameter
            URL url = new URL(ApiService.getBaseUrl()+"/leave-requests/" + id + "/status?status=" + newStatus);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");


            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                showAlert(Alert.AlertType.INFORMATION, "Leave status updated successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update. Server responded with: " + responseCode);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error occurred: " + ex.getMessage());
        }
    }


    public void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtLeaveId.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
