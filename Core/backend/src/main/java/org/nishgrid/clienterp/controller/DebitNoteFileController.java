package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.DebitNoteFileResponse;
import org.nishgrid.clienterp.model.DebitNote;
import org.nishgrid.clienterp.model.DebitNoteFile;
import org.nishgrid.clienterp.repository.DebitNoteFileRepository;
import org.nishgrid.clienterp.repository.DebitNoteRepository;
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
@RequestMapping("/api/debit-note-files")
public class DebitNoteFileController {

    @Autowired private FileStorageService fileStorageService;
    @Autowired private DebitNoteFileRepository fileRepository;
    @Autowired private DebitNoteRepository debitNoteRepository;

    @PostMapping("/upload")
    public DebitNoteFileResponse uploadFile(@RequestParam("file") MultipartFile file,
                                            @RequestParam("debitNoteId") Long debitNoteId,
                                            @RequestParam("uploadedBy") String uploadedBy) {
        DebitNote debitNote = debitNoteRepository.findById(debitNoteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit Note not found"));

        String fileName = fileStorageService.storeFile(file);

        DebitNoteFile doc = new DebitNoteFile();
        doc.setDebitNote(debitNote);
        doc.setFilePath(fileName);
        doc.setFileType(file.getContentType());
        doc.setUploadedBy(uploadedBy);

        DebitNoteFile savedDoc = fileRepository.save(doc);
        return DebitNoteFileResponse.fromEntity(savedDoc);
    }

    @GetMapping("/debit-note/{debitNoteId}")
    public List<DebitNoteFileResponse> getFilesForDebitNote(@PathVariable("debitNoteId") Long debitNoteId) {
        return fileRepository.findByDebitNoteDebitNoteId(debitNoteId).stream()
                .map(DebitNoteFileResponse::fromEntity)
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
    public ResponseEntity<Void> deleteFile(@PathVariable("id") Long id) {
        DebitNoteFile doc = fileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        fileStorageService.deleteFile(doc.getFilePath());
        fileRepository.delete(doc);

        return ResponseEntity.noContent().build();
    }
}