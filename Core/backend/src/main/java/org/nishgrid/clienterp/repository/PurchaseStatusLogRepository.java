package org.nishgrid.clienterp.repository;
import org.nishgrid.clienterp.model.PurchaseStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseStatusLogRepository extends JpaRepository<PurchaseStatusLog, Long> {
    @Query("SELECT log FROM PurchaseStatusLog log JOIN FETCH log.purchaseOrder po JOIN FETCH po.vendor ORDER BY log.changedAt DESC")
    List<PurchaseStatusLog> findAllWithDetails();
}