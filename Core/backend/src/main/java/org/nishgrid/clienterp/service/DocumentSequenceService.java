package org.nishgrid.clienterp.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.nishgrid.clienterp.model.DocumentSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DocumentSequenceService {

    @Autowired
    private EntityManager entityManager;

    /**
     * Generates the next credit note number in a transaction-safe way.
     * propagation = REQUIRES_NEW ensures this runs in its own isolated transaction.
     * LockModeType.PESSIMISTIC_WRITE locks the database row to prevent race conditions.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getNextCreditNoteNumber() {
        DocumentSequence sequence = entityManager.find(
                DocumentSequence.class,
                "credit_note",
                LockModeType.PESSIMISTIC_WRITE
        );

        if (sequence == null) {
            // This is a safeguard. The row should be created as per the previous instructions.
            throw new RuntimeException("DocumentSequence 'credit_note' not found in the database!");
        }

        long nextValue = sequence.getNextValue();
        sequence.setNextValue(nextValue + 1);
        entityManager.merge(sequence);

        int year = LocalDate.now().getYear();

        return String.format("CN-%d-%04d", year, nextValue);
    }
}