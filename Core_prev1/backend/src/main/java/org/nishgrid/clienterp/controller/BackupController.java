package org.nishgrid.clienterp.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.nishgrid.clienterp.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/export/sql")
    public void exportSql(HttpServletResponse response) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String zipFileName = String.format("erp_backup_%s.sql.zip", timestamp);

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
                backupService.exportSql(zipOut);
            }

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String excelFileName = String.format("erp_backup_%s.xlsx", timestamp);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + excelFileName);

            backupService.exportToExcel(response.getOutputStream());

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
    @PostMapping("/import/sql")
    public ResponseEntity<String> importSql(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            backupService.importSql(file);
            return ResponseEntity.ok("Database restored successfully from " + file.getOriginalFilename());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to restore database: " + e.getMessage());
        }
    }
}