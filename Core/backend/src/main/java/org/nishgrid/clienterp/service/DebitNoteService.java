package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.DebitNoteRequest;
import org.nishgrid.clienterp.dto.DebitNoteResponse;
import java.util.List;

public interface DebitNoteService {
    DebitNoteResponse createDebitNote(DebitNoteRequest request);
    List<DebitNoteResponse> getAllDebitNotes();
    DebitNoteResponse getDebitNoteById(Long id);
    DebitNoteResponse updateDebitNote(Long id, DebitNoteRequest request);
    void deleteDebitNote(Long id);
}