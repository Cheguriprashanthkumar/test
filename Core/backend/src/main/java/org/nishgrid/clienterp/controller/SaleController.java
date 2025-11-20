package org.nishgrid.clienterp.controller;

import jakarta.persistence.EntityNotFoundException;
import org.nishgrid.clienterp.dto.PaymentRequestDTO;
import org.nishgrid.clienterp.dto.SaleRequestDTO;
import org.nishgrid.clienterp.dto.SaleUpdateDTO;
import org.nishgrid.clienterp.dto.SalesListResponse;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.nishgrid.clienterp.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private SalesInvoiceRepository salesInvoiceRepository;

    @PostMapping
    public ResponseEntity<?> saveFullSale(@RequestBody SaleRequestDTO saleRequest) {
        try {
            SalesInvoice savedInvoice = saleService.saveFullSale(saleRequest);
            return new ResponseEntity<>(savedInvoice, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving sale: " + e.getMessage());
        }
    }

    @GetMapping
    public SalesListResponse getSales(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "status", required = false, defaultValue = "PAID,Partially Returned") String status) {
        List<String> statuses = Arrays.asList(status.split(","));
        return saleService.getSalesAuditData(startDate, endDate, statuses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesInvoice> getInvoiceById(@PathVariable("id") Long id) {
        return salesInvoiceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesInvoice> updateSale(
            @PathVariable("id") Long id,
            @RequestBody SaleUpdateDTO updateDto) {
        try {
            SalesInvoice updatedInvoice = saleService.updateSale(id, updateDto);
            return ResponseEntity.ok(updatedInvoice);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadSalesData(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "status", required = false, defaultValue = "PAID,Partially Returned") String status) throws IOException {

        List<String> statuses = Arrays.asList(status.split(","));
        byte[] excelData = saleService.exportSalesToExcel(startDate, endDate, statuses);
        String filename = "sales_data_" + startDate + "_to_" + endDate + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    @GetMapping("/next-invoice-number")
    public ResponseEntity<Map<String, String>> getNextInvoiceNumber() {
        String nextInvoiceNumber = saleService.getNextInvoiceNumber();
        return ResponseEntity.ok(Map.of("nextInvoiceNumber", nextInvoiceNumber));
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<?> addPayment(@PathVariable("id") Long id, @RequestBody PaymentRequestDTO paymentRequest) {
        try {
            SalesInvoice updatedInvoice = saleService.addPayment(id, paymentRequest);
            return ResponseEntity.ok(updatedInvoice);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }
}