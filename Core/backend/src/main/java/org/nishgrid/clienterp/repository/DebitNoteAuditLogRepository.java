package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.DebitNoteAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebitNoteAuditLogRepository extends JpaRepository<DebitNoteAuditLog, Long> {

    @Query("SELECT dnal FROM DebitNoteAuditLog dnal JOIN FETCH dnal.debitNote dn ORDER BY dnal.changedAt DESC")
    List<DebitNoteAuditLog> findAllWithDetails();
}