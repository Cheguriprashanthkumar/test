package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.CancelledExpenseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CancelledExpenseLogRepository extends JpaRepository<CancelledExpenseLog, Long> {
    @Query("SELECT log FROM CancelledExpenseLog log JOIN FETCH log.otherExpense ORDER BY log.cancelledOn DESC")
    List<CancelledExpenseLog> findAllWithDetails();
}