package org.nishgrid.clienterp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.PayslipFx;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import org.nishgrid.clienterp.service.ApiService;
public class EditPayslipController {
    @FXML private TextField txtEmployeeCode, txtMonth, txtDaysPresent, txtTotalSalary, txtDeductions, txtNetSalary;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Button btnSave;
    private PayslipFx payslip;

    public void initialize() {
        cbStatus.getItems().addAll("Unpaid", "Paid", "Pending");
        btnSave.setOnAction(e -> {
            try {
                JSONObject json = new JSONObject();
                json.put("employeeCode", txtEmployeeCode.getText());
                json.put("month", txtMonth.getText());
                json.put("totalWorkingDays", /* p.getTotalWorkingDays() */ 0);
                json.put("daysPresent", Integer.parseInt(txtDaysPresent.getText()));
                json.put("totalSalary", new java.math.BigDecimal(txtTotalSalary.getText()));
                json.put("totalDeductions", new java.math.BigDecimal(txtDeductions.getText()));
                json.put("netSalary", new java.math.BigDecimal(txtNetSalary.getText()));
                json.put("paymentStatus", cbStatus.getValue());

                URL url = new URL(ApiService.getBaseUrl()+"/payslips/" + payslip.getPayslipId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setDoOutput(true);
                try (var os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes());
                }
                if (conn.getResponseCode()!=200) throw new RuntimeException("HTTP " + conn.getResponseCode());
                conn.disconnect();
                ((Stage)btnSave.getScene().getWindow()).close();
            } catch (Exception ex){ ex.printStackTrace(); }
        });
    }

    public void setPayslip(PayslipFx p) {
        this.payslip = p;
        txtEmployeeCode.setText(p.getEmployeeCode());
        txtMonth.setText(p.getMonth());
        txtDaysPresent.setText(String.valueOf(p.getDaysPresent()));
        txtTotalSalary.setText(p.getTotalSalary().toPlainString());
        txtDeductions.setText(p.getTotalDeductions().toPlainString());
        txtNetSalary.setText(p.getNetSalary().toPlainString());
        cbStatus.setValue(p.getPaymentStatus());
    }
}
