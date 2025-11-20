package org.nishgrid.clienterp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.nishgrid.clienterp.model.PayslipFx;
import org.nishgrid.clienterp.service.ApiService;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class PayslipTabController {
    @FXML private TextField searchEmployeeCode;
    @FXML private TableView<PayslipFx> payslipTable;
    @FXML private TableColumn<PayslipFx, Integer> colPayslipId;
    @FXML private TableColumn<PayslipFx, String> colEmpCode;
    @FXML private TableColumn<PayslipFx, String> colMonth;
    @FXML private TableColumn<PayslipFx, String> colTotalSalary;
    @FXML private TableColumn<PayslipFx, String> colDeductions;
    @FXML private TableColumn<PayslipFx, String> colNetSalary;
    @FXML private TableColumn<PayslipFx, String> colStatus;

    private final ObservableList<PayslipFx> allPayslips = FXCollections.observableArrayList();

    @FXML public void initialize() {
        setupColumns();
        loadPayslips();
    }

    private void setupColumns() {
        colPayslipId.setCellValueFactory(new PropertyValueFactory<>("payslipId"));
        colEmpCode.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEmployeeCode()));
        colMonth.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getMonth()));
        colTotalSalary.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTotalSalary().toPlainString()));
        colDeductions.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTotalDeductions().toPlainString()));
        colNetSalary.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getNetSalary().toPlainString()));
        colStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getPaymentStatus()));

        TableColumn<PayslipFx, Void> editCol = new TableColumn<>("Edit");
        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Edit");
            {
                btn.setOnAction(e -> {
                    PayslipFx p = getTableView().getItems().get(getIndex());
                    openEditDialog(p);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        payslipTable.getColumns().add(editCol);
    }

    private void loadPayslips() {
        try {
            URL url = new URL(ApiService.getBaseUrl()+"/payslips");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) throw new IOException("HTTP " + conn.getResponseCode());
            String json = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    .lines().collect(Collectors.joining());
            conn.disconnect();

            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            List<PayslipFx> list = mapper.readValue(json, new TypeReference<>() {});
            allPayslips.setAll(list);
            payslipTable.setItems(allPayslips);
        } catch (Exception ex) {
            ex.printStackTrace(); /* show alert here */
        }
    }

    @FXML private void onSearch() {
        String code = searchEmployeeCode.getText().trim().toLowerCase();
        ObservableList<PayslipFx> filtered = allPayslips.filtered(p ->
                code.isEmpty() ||
                        (p.getEmployeeCode() != null && p.getEmployeeCode().toLowerCase().contains(code))
        );
        payslipTable.setItems(filtered);
    }

    @FXML private void onReset() {
        searchEmployeeCode.clear();
        payslipTable.setItems(allPayslips);
    }

    private void openEditDialog(PayslipFx p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_payslip.fxml"));
            Parent root = loader.load();
            EditPayslipController ctl = loader.getController();
            ctl.setPayslip(p);
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setScene(new Scene(root));
            dlg.setTitle("Edit Payslip");
            dlg.showAndWait();
            loadPayslips(); // refresh after edit
        } catch (Exception e) { e.printStackTrace(); }
    }
}
