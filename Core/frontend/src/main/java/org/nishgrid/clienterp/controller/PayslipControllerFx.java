package org.nishgrid.clienterp.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.nishgrid.clienterp.dto.PayslipRequest;
import org.nishgrid.clienterp.model.EmployeeFx; // Correct import
import org.nishgrid.clienterp.model.PayslipFx;
import org.nishgrid.clienterp.service.ApiService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PayslipControllerFx {

    // --- DECLARED ONCE WITH THE CORRECT CLASS ---
    @FXML private ComboBox<EmployeeFx> employeeComboBox;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private TextField totalWorkingDaysField;
    @FXML private TextField daysPresentField;
    @FXML private TextField totalSalaryField;
    @FXML private TextField totalDeductionsField;
    @FXML private TextField netSalaryField;
    @FXML private Label statusLabel;

    // Table View
    @FXML private TableView<PayslipFx> payslipTable;
    @FXML private TableColumn<PayslipFx, String> employeeCodeColumn;
    @FXML private TableColumn<PayslipFx, String> monthColumn;
    @FXML private TableColumn<PayslipFx, BigDecimal> netSalaryColumn;
    @FXML private TableColumn<PayslipFx, String> statusColumn;

    // Buttons
    @FXML private Button downloadButton;

    private final ApiService apiService = new ApiService();
    private final ObservableList<PayslipFx> payslipList = FXCollections.observableArrayList();
    private PauseTransition searchDebounce; // For delaying search API calls

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearchableEmployeeComboBox();
        setupMonthComboBox();
        addSelectionListeners();
        loadInitialData();
    }

    private void setupTableColumns() {
        employeeCodeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        netSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("netSalary"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        payslipTable.setItems(payslipList);
    }

    private void setupSearchableEmployeeComboBox() {
        searchDebounce = new PauseTransition(Duration.millis(500));

        // --- UPDATED StringConverter TO USE EmployeeFx ---
        employeeComboBox.setConverter(new StringConverter<EmployeeFx>() {
            @Override
            public String toString(EmployeeFx employee) {
                return employee == null ? "" : employee.getFullName() + " (" + employee.getEmployeeCode() + ")";
            }

            @Override
            public EmployeeFx fromString(String string) {
                return employeeComboBox.getItems().stream()
                        .filter(e -> (e.getFullName() + " (" + e.getEmployeeCode() + ")").equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        employeeComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && employeeComboBox.getSelectionModel().getSelectedItem() != null &&
                    newValue.equals(employeeComboBox.getConverter().toString(employeeComboBox.getSelectionModel().getSelectedItem()))) {
                return;
            }
            searchDebounce.setOnFinished(e -> performSearch(newValue));
            searchDebounce.playFromStart();
        });
    }

    private void performSearch(String query) {
        if (query == null || query.trim().length() < 2) {
            Platform.runLater(() -> employeeComboBox.getItems().clear());
            return;
        }

        new Thread(() -> {
            // --- UPDATED TO EXPECT List<EmployeeFx> ---
            List<EmployeeFx> searchResult = apiService.searchEmployees(query);
            Platform.runLater(() -> {
                employeeComboBox.setItems(FXCollections.observableArrayList(searchResult));
                if (!searchResult.isEmpty()) {
                    employeeComboBox.show();
                } else {
                    employeeComboBox.hide();
                }
            });
        }).start();
    }

    private void setupMonthComboBox() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth current = YearMonth.now();
        ObservableList<String> months = FXCollections.observableArrayList(
                IntStream.range(0, 12)
                        .mapToObj(current::minusMonths)
                        .map(ym -> ym.format(formatter))
                        .collect(Collectors.toList())
        );
        monthComboBox.setItems(months);
    }

    private void addSelectionListeners() {
        employeeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) {
                triggerAutoPopulation();
            }
        });
        monthComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> triggerAutoPopulation());
    }

    private void triggerAutoPopulation() {
        // --- UPDATED TO USE EmployeeFx ---
        EmployeeFx selectedEmployee = employeeComboBox.getValue();
        String selectedMonth = monthComboBox.getValue();

        if (selectedEmployee != null && selectedMonth != null) {
            PayslipRequest data = apiService.getCalculatedPayslipData(selectedEmployee.getEmployeeCode(), selectedMonth);
            if (data != null) {
                populateForm(data);
                statusLabel.setText("");
            } else {
                statusLabel.setText("Could not fetch calculation data.");
            }
        }
    }

    private void populateForm(PayslipRequest data) {
        totalWorkingDaysField.setText(String.valueOf(data.getTotalWorkingDays()));
        daysPresentField.setText(String.valueOf(data.getDaysPresent()));
        totalSalaryField.setText(data.getTotalSalary().toPlainString());
        totalDeductionsField.setText(data.getTotalDeductions().toPlainString());
        netSalaryField.setText(data.getNetSalary().toPlainString());
    }

    @FXML
    private void handleSave() {
        // --- UPDATED TO USE EmployeeFx ---
        EmployeeFx selectedEmployee = employeeComboBox.getValue();
        if (selectedEmployee == null || monthComboBox.getValue() == null) {
            statusLabel.setText("Error: Please select an employee and month.");
            return;
        }

        try {
            PayslipRequest request = new PayslipRequest();
            request.setEmployeeCode(selectedEmployee.getEmployeeCode());
            request.setMonth(monthComboBox.getValue());
            request.setTotalWorkingDays(Integer.parseInt(totalWorkingDaysField.getText()));
            request.setDaysPresent(Integer.parseInt(daysPresentField.getText()));
            request.setTotalSalary(new BigDecimal(totalSalaryField.getText()));
            request.setTotalDeductions(new BigDecimal(totalDeductionsField.getText()));
            request.setNetSalary(new BigDecimal(netSalaryField.getText()));

            HttpResponse<String> response = apiService.savePayslip(request);

            if (response.statusCode() == 200) {
                statusLabel.setText("Success: Payslip saved!");
                loadInitialData();
                handleClear();
            } else if (response.statusCode() == 409) {
                statusLabel.setText("Error: " + response.body());
            } else {
                statusLabel.setText("Error: Failed to save. Status: " + response.statusCode());
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Please enter valid numbers in the salary fields.");
        } catch (Exception e) {
            statusLabel.setText("Error: An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear() {
        employeeComboBox.getSelectionModel().clearSelection();
        employeeComboBox.getEditor().clear();
        employeeComboBox.getItems().clear();
        monthComboBox.getSelectionModel().clearSelection();
        totalWorkingDaysField.clear();
        daysPresentField.clear();
        totalSalaryField.clear();
        totalDeductionsField.clear();
        netSalaryField.clear();
        statusLabel.setText("");
        payslipTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleDownload() {
        PayslipFx selectedPayslip = payslipTable.getSelectionModel().getSelectedItem();
        if (selectedPayslip == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a payslip from the table to download.").show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Payslip PDF");
        fileChooser.setInitialFileName("Payslip_" + selectedPayslip.getEmployeeCode() + "_" + selectedPayslip.getMonth() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(downloadButton.getScene().getWindow());

        if (file != null) {
            try {
                generatePdf(selectedPayslip, file.getAbsolutePath());
                new Alert(Alert.AlertType.INFORMATION, "PDF downloaded successfully to:\n" + file.getAbsolutePath()).show();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to generate PDF: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    private void generatePdf(PayslipFx payslip, String path) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(200, 750);
                contentStream.showText("Payslip for " + payslip.getMonth());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 700);

                contentStream.showText("Employee Code: " + payslip.getEmployeeCode());
                contentStream.newLine();
                contentStream.showText("Employee Name: " + (payslip.getEmployee() != null ? payslip.getEmployee().getFullName() : "N/A"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Total Working Days: " + payslip.getTotalWorkingDays());
                contentStream.newLine();
                contentStream.showText("Days Present: " + payslip.getDaysPresent());
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Gross Salary: " + payslip.getTotalSalary());
                contentStream.newLine();
                contentStream.showText("Total Deductions: " + payslip.getTotalDeductions());
                contentStream.newLine();
                contentStream.newLine();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.showText("Net Salary: " + payslip.getNetSalary());
                contentStream.endText();
            }
            document.save(path);
        }
    }

    private void loadInitialData() {
        payslipList.setAll(apiService.getAllPayslips());
    }
}