package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.nishgrid.clienterp.dto.SalaryStructureRequest;
import org.nishgrid.clienterp.model.Salary;
import org.nishgrid.clienterp.service.ApiService;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditSalaryController {

    @FXML private TextField txtBasic, txtHRA, txtAllowances, txtPF, txtESI, txtTDS;

    private Salary salary; // ✅ Use Salary object

    public void setSalary(Salary salary) {
        this.salary = salary;

        txtBasic.setText(String.valueOf(salary.getBasicSalary()));
        txtHRA.setText(String.valueOf(salary.getHra()));
        txtAllowances.setText(String.valueOf(salary.getOtherAllowances()));
        txtPF.setText(String.valueOf(salary.getPfDeduction()));
        txtESI.setText(String.valueOf(salary.getEsiDeduction()));
        txtTDS.setText(String.valueOf(salary.getTdsDeduction()));
    }

    @FXML

    public void onSave() {
        try {
            SalaryStructureRequest request = new SalaryStructureRequest();
            request.setEmployeeCode(salary.getEmployee().getEmployeeCode());
            request.setBasicSalary(new BigDecimal(txtBasic.getText()));
            request.setHra(new BigDecimal(txtHRA.getText()));
            request.setOtherAllowances(new BigDecimal(txtAllowances.getText()));
            request.setPfDeduction(new BigDecimal(txtPF.getText()));
            request.setEsiDeduction(new BigDecimal(txtESI.getText()));
            request.setTdsDeduction(new BigDecimal(txtTDS.getText()));

            // ✅ Fixing the URL with a proper '/' before ID
            URL url = new URL(ApiService.getBaseUrl()+"/salary/" + salary.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // ✅ Write JSON body
            ObjectMapper mapper = new ObjectMapper();
            try (OutputStream os = conn.getOutputStream()) {
                mapper.writeValue(os, request);
            }

            // ✅ Handle response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Optionally show a success message or alert
                System.out.println("Salary structure updated successfully.");
                ((Stage) txtBasic.getScene().getWindow()).close();
            } else {
                System.err.println("Failed to update salary. HTTP code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
