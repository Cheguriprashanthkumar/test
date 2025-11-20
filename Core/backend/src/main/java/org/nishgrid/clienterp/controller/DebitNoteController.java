package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.DebitNoteRequest;
import org.nishgrid.clienterp.dto.DebitNoteResponse;
import org.nishgrid.clienterp.service.DebitNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/debit-notes")
public class DebitNoteController {

    @Autowired
    private DebitNoteService debitNoteService;

    @PostMapping
    public ResponseEntity<DebitNoteResponse> createDebitNote(@RequestBody DebitNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(debitNoteService.createDebitNote(request));
    }

    @GetMapping
    public List<DebitNoteResponse> getAllDebitNotes() {
        return debitNoteService.getAllDebitNotes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebitNoteResponse> getDebitNoteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(debitNoteService.getDebitNoteById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebitNoteResponse> updateDebitNote(@PathVariable("id") Long id, @RequestBody DebitNoteRequest request) {
        return ResponseEntity.ok(debitNoteService.updateDebitNote(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDebitNote(@PathVariable("id") Long id) {
        debitNoteService.deleteDebitNote(id);
        return ResponseEntity.noContent().build();
    }
}