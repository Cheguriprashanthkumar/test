package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.DebitNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DebitNoteRepository extends JpaRepository<DebitNote, Long> {

    @Query("SELECT DISTINCT dn FROM DebitNote dn JOIN FETCH dn.vendor LEFT JOIN FETCH dn.purchaseInvoice LEFT JOIN FETCH dn.items items LEFT JOIN FETCH items.item ORDER BY dn.debitNoteDate DESC")
    List<DebitNote> findAllWithDetails();

    @Query("SELECT dn FROM DebitNote dn JOIN FETCH dn.vendor LEFT JOIN FETCH dn.purchaseInvoice LEFT JOIN FETCH dn.items items LEFT JOIN FETCH items.item WHERE dn.debitNoteId = :id")
    Optional<DebitNote> findByIdWithDetails(@Param("id") Long id);
}