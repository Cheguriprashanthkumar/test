package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.CreditNoteFileResponse;
import org.nishgrid.clienterp.service.CreditNoteFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/credit-notes")
@RequiredArgsConstructor
public class CreditNoteFileController {

    private final CreditNoteFileService fileService;

    @GetMapping("/{id}/files")
    public ResponseEntity<List<CreditNoteFileResponse>> getFilesByCreditNoteId(@PathVariable("id") Long creditNoteId) {
        List<CreditNoteFileResponse> files = fileService.getFilesForCreditNote(creditNoteId);
        return ResponseEntity.ok(files);
    }

    @PostMapping("/{id}/files")
    public ResponseEntity<CreditNoteFileResponse> uploadFile(
            @PathVariable("id") Long creditNoteId,
            @RequestParam("file") MultipartFile file) {

        CreditNoteFileResponse response = fileService.attachFileToCreditNote(creditNoteId, file);
        return ResponseEntity.ok(response);
    }
}