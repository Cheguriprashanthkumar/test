package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.CreditNoteAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditNoteAuditLogRepository extends JpaRepository<CreditNoteAuditLog, Long> {
}