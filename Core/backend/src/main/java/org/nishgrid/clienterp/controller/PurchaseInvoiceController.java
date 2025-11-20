package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.PurchaseInvoiceRequest;
import org.nishgrid.clienterp.dto.PurchaseInvoiceResponse;
import org.nishgrid.clienterp.service.PurchaseInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-invoices")
public class PurchaseInvoiceController {

    @Autowired
    private PurchaseInvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<PurchaseInvoiceResponse> createInvoice(@RequestBody PurchaseInvoiceRequest request) {
        PurchaseInvoiceResponse createdInvoice = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
    }

    @GetMapping
    public List<PurchaseInvoiceResponse> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseInvoiceResponse> getInvoiceById(@PathVariable("id") Long id) {
        PurchaseInvoiceResponse invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseInvoiceResponse> updateInvoice(@PathVariable("id") Long id, @RequestBody PurchaseInvoiceRequest request) {
        PurchaseInvoiceResponse updatedInvoice = invoiceService.updateInvoice(id, request);
        return ResponseEntity.ok(updatedInvoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable("id") Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}