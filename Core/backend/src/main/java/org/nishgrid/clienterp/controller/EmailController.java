package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.nishgrid.clienterp.service.EmailService;
import org.nishgrid.clienterp.service.InvoicePrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.io.IOException;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired private EmailService emailService;
    @Autowired private SalesInvoiceRepository salesInvoiceRepository;
    @Autowired private InvoicePrintService invoicePrintService;

    @PostMapping("/send/invoice/{invoiceId}")
    public ResponseEntity<Void> sendInvoiceEmail(@PathVariable("invoiceId") Long invoiceId) {
        SalesInvoice invoice = salesInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found with id: " + invoiceId));

        if (invoice.getCustomer() == null || invoice.getCustomer().getEmail() == null || invoice.getCustomer().getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer email is not available for this invoice.");
        }

        String subject = "Your Invoice from [Your Company Name]: " + invoice.getInvoiceNo();
        String body = "Dear " + invoice.getCustomer().getName() + ",\n\nAs requested, please find your invoice attached.\n\nBest Regards,\n[Your Company Name]";

        byte[] pdfBytes;
        try {
            pdfBytes = invoicePrintService.generateInvoicePdf(invoiceId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate invoice PDF.", e);
        }

        String attachmentName = "Invoice-" + invoice.getInvoiceNo() + ".pdf";

        emailService.sendInvoiceEmail(invoice, subject, body, pdfBytes, attachmentName);

        return ResponseEntity.accepted().build();
    }
}
