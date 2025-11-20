package org.nishgrid.clienterp.controller;

import jakarta.validation.Valid;
import org.nishgrid.clienterp.dto.ReportGenerationRequest;
import org.nishgrid.clienterp.dto.ReportRequest;
import org.nishgrid.clienterp.model.SalesSummaryReport;
import org.nishgrid.clienterp.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private SalesReportService reportService;




    @PostMapping("/generate")
    public SalesSummaryReport generateReport(@Valid @RequestBody ReportGenerationRequest request) {
        return reportService.generateReport(request);
    }


    @GetMapping
    public List<SalesSummaryReport> getReports(@RequestParam(value = "type", required = false) String type) {
        return reportService.getReports(type);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable("id") Long id) throws IOException {
        byte[] excelData = reportService.downloadReportAsExcel(id);
        String filename = "sales_report_" + id + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }


}