package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.SalesSummaryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesSummaryReportRepository extends JpaRepository<SalesSummaryReport, Long> {

    /**
     * Finds all reports of a specific type (DAILY, MONTHLY, or YEARLY) and
     * orders them by the report date, with the most recent appearing first.
     * Spring Data JPA automatically creates the query from this method name.
     */

    List<SalesSummaryReport> findByReportTypeOrderByReportDateDesc(SalesSummaryReport.ReportType reportType);

}