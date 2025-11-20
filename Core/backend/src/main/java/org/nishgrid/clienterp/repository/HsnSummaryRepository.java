package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.HsnSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface HsnSummaryRepository extends JpaRepository<HsnSummary, Long> {
    List<HsnSummary> findByMonth(String month);

    @Transactional
    @Modifying
    @Query("DELETE FROM HsnSummary h WHERE h.month = :month")
    void deleteByMonth(String month);
}
