package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.Gstr1ItemDTO;
import org.nishgrid.clienterp.model.GstExport;
import org.nishgrid.clienterp.model.Gstr1Item;
import org.nishgrid.clienterp.model.Gstr3bSummary;
import org.nishgrid.clienterp.repository.Gstr1ItemRepository;
import org.nishgrid.clienterp.service.GstExportService;
import org.nishgrid.clienterp.service.GstReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gst")
public class GstController {

    @Autowired
    private Gstr1ItemRepository gstr1ItemRepository;

    @Autowired
    private GstReportService gstReportService;
    @Autowired
    private GstExportService gstExportService;



    @GetMapping("/exports")
    public List<GstExport> getAllExports() {
        return gstExportService.getAllExports();
    }

    @PostMapping("/exports/generate")
    public GstExport generateExport(@RequestParam("monthYear") String monthYear) {
        YearMonth ym = YearMonth.parse(monthYear);
        return gstExportService.generateGstr1CsvExport(ym);
    }

    @GetMapping("/exports/{id}/download")
    public ResponseEntity<byte[]> downloadExport(@PathVariable("id") Long id) throws Exception {
        byte[] csvData = gstExportService.downloadExportFile(id);
        String filename = "gstr1_export_file_" + id + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvData);
    }
    @GetMapping("/gstr1-items")
    public List<Gstr1ItemDTO> getAllGstr1Items() {
        return gstr1ItemRepository.findAll().stream()
                .map(this::convertToGstr1Dto)
                .collect(Collectors.toList());
    }

    @GetMapping("/gstr3b-summaries")
    public List<Gstr3bSummary> getAllGstr3bSummaries() {
        return gstReportService.getAllGstr3bSummaries();
    }


    @PostMapping("/gstr3b-summaries/generate")
    public Gstr3bSummary generateGstr3bSummary(@RequestParam("monthYear") String monthYear) {
        YearMonth ym = YearMonth.parse(monthYear);
        return gstReportService.generateAndSaveGstr3bSummary(ym);
    }

    private Gstr1ItemDTO convertToGstr1Dto(Gstr1Item item) {
        Gstr1ItemDTO dto = new Gstr1ItemDTO();
        dto.setId(item.getId());
        dto.setInvoiceId(item.getSalesInvoice().getInvoiceId());
        dto.setCustomerGstin(item.getCustomerGstin());
        dto.setInvoiceDate(item.getInvoiceDate());
        dto.setItemHsn(item.getItemHsn());
        dto.setTaxableValue(item.getTaxableValue());
        dto.setGstRate(item.getGstRate());
        dto.setCgstAmount(item.getCgstAmount());
        dto.setSgstAmount(item.getSgstAmount());
        dto.setIgstAmount(item.getIgstAmount());
        dto.setExportMonth(item.getExportMonth());
        return dto;
    }
}