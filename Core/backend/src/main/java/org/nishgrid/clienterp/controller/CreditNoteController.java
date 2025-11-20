package org.nishgrid.clienterp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.CreditNotePaymentRequest;
import org.nishgrid.clienterp.dto.CreditNoteRequest;
import org.nishgrid.clienterp.dto.CreditNoteResponse;
import org.nishgrid.clienterp.service.CreditNoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-notes")
@RequiredArgsConstructor
public class CreditNoteController {

    private final CreditNoteService creditNoteService;

    @PostMapping
    public ResponseEntity<CreditNoteResponse> createCreditNote(@Valid @RequestBody CreditNoteRequest request) {
        CreditNoteResponse response = creditNoteService.createCreditNote(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CreditNoteResponse>> getAllCreditNotes() {
        return ResponseEntity.ok(creditNoteService.getAllCreditNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditNoteResponse> getCreditNoteById(@PathVariable("id") Long id) { // <-- FIX HERE
        return ResponseEntity.ok(creditNoteService.getCreditNoteById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditNoteResponse> updateCreditNote(@PathVariable("id") Long id, @Valid @RequestBody CreditNoteRequest request) { // <-- FIX HERE
        return ResponseEntity.ok(creditNoteService.updateCreditNote(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCreditNote(@PathVariable("id") Long id) { // <-- FIX HERE
        creditNoteService.deleteCreditNote(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/payments")
    public ResponseEntity<CreditNoteResponse> addPayment(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreditNotePaymentRequest paymentRequest) {

        CreditNoteResponse response = creditNoteService.addPayment(id, paymentRequest);
        return ResponseEntity.ok(response);
    }
}