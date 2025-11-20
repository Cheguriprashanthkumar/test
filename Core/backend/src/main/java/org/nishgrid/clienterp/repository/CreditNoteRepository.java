package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.CreditNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditNoteRepository extends JpaRepository<CreditNote, Long> {


    @Query("SELECT DISTINCT cn FROM CreditNote cn " +
            "LEFT JOIN FETCH cn.customer " +
            "LEFT JOIN FETCH cn.originalInvoice " +
            "ORDER BY cn.creditNoteDate DESC")
    List<CreditNote> findAllWithDetails();


    @Query("SELECT cn FROM CreditNote cn " +
            "LEFT JOIN FETCH cn.customer " +
            "LEFT JOIN FETCH cn.originalInvoice " +
            "LEFT JOIN FETCH cn.items " +
            "LEFT JOIN FETCH cn.taxes " +
            "LEFT JOIN FETCH cn.payments " +
            "LEFT JOIN FETCH cn.files " +
            "WHERE cn.creditNoteId = :id")
    Optional<CreditNote> findByIdWithDetails(@Param("id") Long id);
}