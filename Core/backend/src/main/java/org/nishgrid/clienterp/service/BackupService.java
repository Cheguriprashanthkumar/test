package org.nishgrid.clienterp.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nishgrid.clienterp.model.BackupLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {

    @Autowired
    private BackupLogService backupLogService;

    @Autowired
    private DataSource dataSource;

    @Value("${backup.db.name}")
    private String dbName;
    @Value("${backup.db.username}")
    private String dbUsername;
    @Value("${backup.db.password}")
    private String dbPassword;

    public void exportSql(ZipOutputStream zipOut) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = String.format("erp_backup_%s.sql", timestamp);
        long fileSize = 0;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "mysqldump",
                    "-u", dbUsername,
                    "-p" + dbPassword,
                    dbName
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            zipOut.putNextEntry(new ZipEntry(fileName));
            try (InputStream is = process.getInputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                long totalBytes = 0;
                while ((length = is.read(buffer)) >= 0) {
                    zipOut.write(buffer, 0, length);
                    totalBytes += length;
                }
                fileSize = totalBytes;
            }
            zipOut.closeEntry();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                backupLogService.createLog(BackupLog.ActionType.Export, fileName + ".zip", fileSize, "admin", BackupLog.Status.Success, "Database exported successfully.");
            } else {
                throw new IOException("mysqldump command failed with exit code " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            backupLogService.createLog(BackupLog.ActionType.Export, fileName + ".zip", 0L, "admin", BackupLog.Status.Failed, e.getMessage());
            throw new RuntimeException("Failed to export database backup.", e);
        }
    }

    public void exportToExcel(OutputStream outputStream) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = String.format("erp_backup_%s.xlsx", timestamp);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             Workbook workbook = new XSSFWorkbook()) {

            List<String> tableNames = new ArrayList<>();
            try (ResultSet rs = connection.getMetaData().getTables(dbName, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }

            for (String tableName : tableNames) {
                Sheet sheet = workbook.createSheet(tableName);

                try (ResultSet rs = statement.executeQuery("SELECT * FROM " + tableName)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    Row headerRow = sheet.createRow(0);
                    for (int i = 1; i <= columnCount; i++) {
                        Cell cell = headerRow.createCell(i - 1);
                        cell.setCellValue(metaData.getColumnName(i));
                    }

                    int rowNum = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        for (int i = 1; i <= columnCount; i++) {
                            Cell cell = row.createCell(i - 1);
                            Object value = rs.getObject(i);
                            cell.setCellValue(value != null ? value.toString() : "");
                        }
                    }
                }
            }

            workbook.write(outputStream);

            backupLogService.createLog(BackupLog.ActionType.Export, fileName, 0L, "admin", BackupLog.Status.Success, "Database exported to Excel successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            backupLogService.createLog(BackupLog.ActionType.Export, fileName, 0L, "admin", BackupLog.Status.Failed, e.getMessage());
            throw new RuntimeException("Failed to export database to Excel.", e);
        }
    }
    public void importSql(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());


        File tempFile;
        try {
            tempFile = File.createTempFile("restore-", ".sql");
            file.transferTo(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            backupLogService.createLog(BackupLog.ActionType.Import, fileName, file.getSize(), "admin", BackupLog.Status.Failed, "Failed to save temporary file.");
            throw new RuntimeException("Could not save uploaded file.", e);
        }

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "mysql",
                    "-u", dbUsername,
                    "-p" + dbPassword,
                    dbName
            );
            processBuilder.redirectInput(tempFile); // Redirect the .sql file as input to the command

            // 3. Execute the command
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            // 4. Log the result
            if (exitCode == 0) {
                backupLogService.createLog(BackupLog.ActionType.Import, fileName, file.getSize(), "admin", BackupLog.Status.Success, "Database restored successfully.");
            } else {
                // In a real app, you would capture and log the error stream from the process
                throw new IOException("mysql command failed with exit code " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            backupLogService.createLog(BackupLog.ActionType.Import, fileName, file.getSize(), "admin", BackupLog.Status.Failed, e.getMessage());
            throw new RuntimeException("Failed to restore database backup.", e);
        } finally {
            // 5. Clean up by deleting the temporary file
            tempFile.delete();
        }
    }
}