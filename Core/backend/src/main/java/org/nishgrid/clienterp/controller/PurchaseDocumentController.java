package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.PurchaseDocumentResponse;
import org.nishgrid.clienterp.model.PurchaseDocument;
import org.nishgrid.clienterp.model.PurchaseInvoice;
import org.nishgrid.clienterp.repository.PurchaseDocumentRepository;
import org.nishgrid.clienterp.repository.PurchaseInvoiceRepository;
import org.nishgrid.clienterp.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchase-documents")
public class PurchaseDocumentController {

    @Autowired private FileStorageService fileStorageService;
    @Autowired private PurchaseDocumentRepository documentRepository;
    @Autowired private PurchaseInvoiceRepository invoiceRepository;

    @PostMapping("/upload")
    public PurchaseDocumentResponse uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam("invoiceId") Long invoiceId,
                                               @RequestParam("uploadedBy") String uploadedBy) {
        PurchaseInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        String fileName = fileStorageService.storeFile(file);

        PurchaseDocument doc = new PurchaseDocument();
        doc.setPurchaseInvoice(invoice);
        doc.setFileName(fileName);
        doc.setFileType(file.getContentType());
        doc.setFilePath(fileStorageService.loadFileAsResource(fileName).toString());
        doc.setUploadedBy(uploadedBy);

        return PurchaseDocumentResponse.fromEntity(documentRepository.save(doc));
    }

    @GetMapping("/invoice/{invoiceId}")
    public List<PurchaseDocumentResponse> getDocumentsForInvoice(@PathVariable("invoiceId") Long invoiceId) {
        return documentRepository.findByPurchaseInvoiceId(invoiceId).stream()
                .map(PurchaseDocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") Long id) {
        PurchaseDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        fileStorageService.deleteFile(doc.getFileName());
        documentRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}