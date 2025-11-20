package org.nishgrid.clienterp.service;

import jakarta.validation.Valid;
import org.nishgrid.clienterp.dto.CreditNotePaymentRequest;
import org.nishgrid.clienterp.dto.CreditNoteRequest;
import org.nishgrid.clienterp.dto.CreditNoteResponse;
import java.util.List;

public interface CreditNoteService {
    CreditNoteResponse createCreditNote(CreditNoteRequest request);
    CreditNoteResponse getCreditNoteById(Long id);
    List<CreditNoteResponse> getAllCreditNotes();
    CreditNoteResponse updateCreditNote(Long id, CreditNoteRequest request);
    void deleteCreditNote(Long id);
    CreditNoteResponse addPayment(Long creditNoteId, @Valid CreditNotePaymentRequest paymentRequest);
}