package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.model.GstExport;
import org.nishgrid.clienterp.model.Gstr1Item;
import org.nishgrid.clienterp.repository.GstExportRepository;
import org.nishgrid.clienterp.repository.Gstr1ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GstExportService {

    @Autowired private GstExportRepository exportRepository;
    @Autowired private Gstr1ItemRepository gstr1ItemRepository;

    private final Path exportDir = Paths.get("exports");

    public GstExport generateGstr1CsvExport(YearMonth yearMonth) {
        try {
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }

            String monthYear = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            List<Gstr1Item> items = gstr1ItemRepository.findByExportMonth(monthYear); // Assumes this method exists

            String filename = "gstr1_export_" + monthYear.replace("-", "") + ".csv";
            File exportFile = exportDir.resolve(filename).toFile();

            try (PrintWriter writer = new PrintWriter(exportFile)) {
                // CSV Header
                writer.println("InvoiceID,CustomerGSTIN,InvoiceDate,HSN,TaxableValue,GSTRate,CGST,SGST,IGST");
                // CSV Rows
                items.forEach(item -> writer.printf("%d,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                        item.getSalesInvoice().getInvoiceId(), item.getCustomerGstin(), item.getInvoiceDate(),
                        item.getItemHsn(), item.getTaxableValue(), item.getGstRate(),
                        item.getCgstAmount(), item.getSgstAmount(), item.getIgstAmount()));
            }

            GstExport exportLog = new GstExport();
            exportLog.setMonthYear(monthYear);
            exportLog.setGeneratedOn(LocalDateTime.now());
            exportLog.setFilePath(exportFile.getAbsolutePath());
            exportLog.setStatus(GstExport.GstExportStatus.GENERATED);

            return exportRepository.save(exportLog);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate GST export file.", e);
        }
    }

    public List<GstExport> getAllExports() {
        return exportRepository.findAll();
    }

    public byte[] downloadExportFile(Long exportId) throws Exception {
        GstExport exportLog = exportRepository.findById(exportId).orElseThrow();
        return Files.readAllBytes(Paths.get(exportLog.getFilePath()));
    }
}