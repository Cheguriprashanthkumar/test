package org.nishgrid.clienterp.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nishgrid.clienterp.dto.DailySummaryDTO;
import org.nishgrid.clienterp.dto.ReportGenerationRequest;
import org.nishgrid.clienterp.model.SalesSummaryReport;
import org.nishgrid.clienterp.model.SalesSummaryReport.ReportType;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.nishgrid.clienterp.repository.SalesSummaryReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class SalesReportService {

    @Autowired private SalesInvoiceRepository invoiceRepository;
    @Autowired private SalesSummaryReportRepository reportRepository;

    public SalesSummaryReport generateReport(ReportGenerationRequest request) {
        switch (request.getReportType()) {
            case DAILY:
                return generateAndSaveDailyReport(request.getStartDate());
            case MONTHLY:
                return generateAndSaveMonthlyReport(YearMonth.from(request.getStartDate()));
            case YEARLY:
                return generateAndSaveYearlyReport(request.getStartDate().getYear());
            case FINANCIAL_YEAR:
                return generateAndSaveFinancialYearReport(request.getStartDate().getYear());
            case CUSTOM:
                if (request.getEndDate() == null) {
                    throw new IllegalArgumentException("End date is required for custom range reports.");
                }
                if (request.getStartDate().isAfter(request.getEndDate())) {
                    throw new IllegalArgumentException("Start date cannot be after end date.");
                }
                return generateAndSaveCustomRangeReport(request.getStartDate(), request.getEndDate());
            default:
                throw new IllegalArgumentException("Unsupported report type: " + request.getReportType());
        }
    }

    private SalesSummaryReport generateAndSaveDailyReport(LocalDate date) {
        DailySummaryDTO summary = invoiceRepository.getDailySummary(date);
        SalesSummaryReport report = new SalesSummaryReport();
        report.setReportDate(date);
        report.setReportType(ReportType.DAILY);
        populateReportFromSummary(report, summary);
        return reportRepository.save(report);
    }

    private SalesSummaryReport generateAndSaveMonthlyReport(YearMonth yearMonth) {
        DailySummaryDTO summary = invoiceRepository.getMonthlySummary(yearMonth.getYear(), yearMonth.getMonthValue());
        SalesSummaryReport report = new SalesSummaryReport();
        report.setReportDate(yearMonth.atDay(1));
        report.setReportType(ReportType.MONTHLY);
        populateReportFromSummary(report, summary);
        return reportRepository.save(report);
    }

    private SalesSummaryReport generateAndSaveYearlyReport(int year) {
        DailySummaryDTO summary = invoiceRepository.getYearlySummary(year);
        SalesSummaryReport report = new SalesSummaryReport();
        report.setReportDate(LocalDate.of(year, 1, 1));
        report.setReportType(ReportType.YEARLY);
        populateReportFromSummary(report, summary);
        return reportRepository.save(report);
    }

    private SalesSummaryReport generateAndSaveFinancialYearReport(int startYear) {
        LocalDate start = LocalDate.of(startYear, 4, 1);
        LocalDate end = LocalDate.of(startYear + 1, 3, 31);
        DailySummaryDTO summary = invoiceRepository.getRangeSummary(start, end);
        SalesSummaryReport report = new SalesSummaryReport();
        report.setReportDate(start);
        report.setReportType(ReportType.FINANCIAL_YEAR);
        populateReportFromSummary(report, summary);
        return reportRepository.save(report);
    }

    private SalesSummaryReport generateAndSaveCustomRangeReport(LocalDate startDate, LocalDate endDate) {
        DailySummaryDTO summary = invoiceRepository.getRangeSummary(startDate, endDate);
        SalesSummaryReport report = new SalesSummaryReport();
        report.setReportDate(startDate);
        report.setReportType(ReportType.CUSTOM);
        populateReportFromSummary(report, summary);
        return reportRepository.save(report);
    }

    private void populateReportFromSummary(SalesSummaryReport report, DailySummaryDTO summary) {
        report.setTotalSales(summary.getTotalSales());
        report.setTotalTax(summary.getTotalTax());
        report.setTotalDiscount(summary.getTotalDiscount());
        report.setTotalItemsSold(Math.toIntExact(summary.getTotalItemsSold()));
        report.setTotalCustomers(Math.toIntExact(summary.getTotalCustomers()));
        report.setGeneratedOn(LocalDateTime.now());
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void generateScheduledDailyReport() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generateAndSaveDailyReport(yesterday);
    }



    public List<SalesSummaryReport> getReports(String type) {
        if (type == null || type.isEmpty() || type.equalsIgnoreCase("ALL")) {
            return reportRepository.findAll();
        }
        try {
            return reportRepository.findByReportTypeOrderByReportDateDesc(ReportType.valueOf(type.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    public byte[] downloadReportAsExcel(Long reportId) throws IOException {
        SalesSummaryReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IOException("Report with ID " + reportId + " not found."));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sales Summary");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            String[] headers = {"Metric", "Value"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            createRow(sheet, 1, "Report ID", report.getReportId());
            createRow(sheet, 2, "Report Date", report.getReportDate().toString());
            createRow(sheet, 3, "Report Type", report.getReportType().toString());
            createRow(sheet, 4, "Total Sales (₹)", report.getTotalSales());

            createRow(sheet, 5, "Total Discount (₹)", report.getTotalDiscount());
            createRow(sheet, 6, "Items Sold", report.getTotalItemsSold());
            createRow(sheet, 7, "Total Customers", report.getTotalCustomers());
            createRow(sheet, 8, "Generated On", report.getGeneratedOn().toString());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                return baos.toByteArray();
            }
        }
    }

    private void createRow(Sheet sheet, int rowNum, String label, Object value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);

        if (value instanceof Number) {
            row.createCell(1).setCellValue(((Number) value).doubleValue());
        } else if (value != null) {
            row.createCell(1).setCellValue(value.toString());
        } else {
            row.createCell(1).setCellValue("N/A");
        }
    }
}