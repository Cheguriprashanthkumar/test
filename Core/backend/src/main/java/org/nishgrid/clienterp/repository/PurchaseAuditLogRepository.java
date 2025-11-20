package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.PurchaseAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseAuditLogRepository extends JpaRepository<PurchaseAuditLog, Long> {
    @Query("SELECT log FROM PurchaseAuditLog log ORDER BY log.timestamp DESC")
    List<PurchaseAuditLog> findAllSorted();
}