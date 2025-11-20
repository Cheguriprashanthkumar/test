package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.dto.DailySummaryDTO;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, Long>, JpaSpecificationExecutor<SalesInvoice> {

    @Query("SELECT si FROM SalesInvoice si JOIN FETCH si.customer c " +
            "WHERE lower(si.invoiceNo) LIKE lower(concat('%', :keyword, '%')) OR " +
            "lower(c.name) LIKE lower(concat('%', :keyword, '%')) " +
            "ORDER BY si.invoiceId DESC")
    List<SalesInvoice> searchInvoices(@Param("keyword") String keyword);

    @Query("SELECT new org.nishgrid.clienterp.dto.DailySummaryDTO(" +
            "COALESCE(SUM(s.paidAmount), 0), " +
            "COALESCE(SUM(s.gstAmount), 0), " +
            "COALESCE(SUM(s.discount), 0), " +
            "(SELECT COUNT(si.id) FROM SalesItem si WHERE si.invoice.invoiceDate = :date AND si.invoice.status = 'PAID'), " +
            "COALESCE(COUNT(DISTINCT s.customer.id), 0L)) " +
            "FROM SalesInvoice s WHERE s.invoiceDate = :date AND s.status = 'PAID'")
    DailySummaryDTO getDailySummary(@Param("date") LocalDate date);

    @Query("SELECT new org.nishgrid.clienterp.dto.DailySummaryDTO(" +
            "COALESCE(SUM(s.paidAmount), 0), " +
            "COALESCE(SUM(s.gstAmount), 0), " +
            "COALESCE(SUM(s.discount), 0), " +
            "(SELECT COUNT(si.id) FROM SalesItem si WHERE YEAR(si.invoice.invoiceDate) = :year AND MONTH(si.invoice.invoiceDate) = :month AND si.invoice.status = 'PAID'), " +
            "COALESCE(COUNT(DISTINCT s.customer.id), 0L)) " +
            "FROM SalesInvoice s WHERE YEAR(s.invoiceDate) = :year AND MONTH(s.invoiceDate) = :month AND s.status = 'PAID'")
    DailySummaryDTO getMonthlySummary(@Param("year") int year, @Param("month") int month);

    @Query("SELECT new org.nishgrid.clienterp.dto.DailySummaryDTO(" +
            "COALESCE(SUM(s.paidAmount), 0), " +
            "COALESCE(SUM(s.gstAmount), 0), " +
            "COALESCE(SUM(s.discount), 0), " +
            "(SELECT COUNT(si.id) FROM SalesItem si WHERE YEAR(si.invoice.invoiceDate) = :year AND si.invoice.status = 'PAID'), " +
            "COALESCE(COUNT(DISTINCT s.customer.id), 0L)) " +
            "FROM SalesInvoice s WHERE YEAR(s.invoiceDate) = :year AND s.status = 'PAID'")
    DailySummaryDTO getYearlySummary(@Param("year") int year);

    @Query("SELECT new org.nishgrid.clienterp.dto.DailySummaryDTO(" +
            "COALESCE(SUM(s.paidAmount), 0), " +
            "COALESCE(SUM(s.gstAmount), 0), " +
            "COALESCE(SUM(s.discount), 0), " +
            "(SELECT COUNT(si.id) FROM SalesItem si WHERE si.invoice.invoiceDate BETWEEN :startDate AND :endDate AND si.invoice.status = 'PAID'), " +
            "COALESCE(COUNT(DISTINCT s.customer.id), 0L)) " +
            "FROM SalesInvoice s WHERE s.invoiceDate BETWEEN :startDate AND :endDate AND s.status = 'PAID'")
    DailySummaryDTO getRangeSummary(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT si FROM SalesInvoice si LEFT JOIN FETCH si.salesItems WHERE si.invoiceId = :invoiceId")
    Optional<SalesInvoice> findByIdWithItems(@Param("invoiceId") Long invoiceId);

    @Query("SELECT s.invoiceNo FROM SalesInvoice s ORDER BY s.invoiceId DESC")
    List<String> findInvoiceNumbers(Pageable pageable);

    List<SalesInvoice> findByStatusIgnoreCase(String status);

    @Query("SELECT si FROM SalesInvoice si JOIN FETCH si.customer c " +
            "WHERE si.invoiceDate BETWEEN :startDate AND :endDate AND si.status = :status " +
            "ORDER BY si.invoiceDate DESC")
    List<SalesInvoice> findByInvoiceDateBetweenAndStatusWithCustomer(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status
    );

    @Query("SELECT si FROM SalesInvoice si JOIN FETCH si.customer c " +
            "WHERE si.invoiceDate BETWEEN :startDate AND :endDate AND si.status IN :statuses " +
            "ORDER BY si.invoiceDate DESC")
    List<SalesInvoice> findByInvoiceDateBetweenAndStatusIn(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<String> statuses
    );

    @Query("SELECT si FROM SalesInvoice si JOIN FETCH si.customer WHERE si.invoiceId = :invoiceId")
    Optional<SalesInvoice> findByIdWithCustomer(@Param("invoiceId") Long invoiceId);

    @Query("SELECT si FROM SalesInvoice si JOIN FETCH si.customer c LEFT JOIN FETCH si.salesItems s WHERE si.invoiceId = :invoiceId")
    Optional<SalesInvoice> findByIdWithDetails(@Param("invoiceId") Long invoiceId);

    @Query("SELECT si FROM SalesInvoice si JOIN FETCH si.customer c ORDER BY si.invoiceId DESC")
    List<SalesInvoice> findAllWithCustomer();

    List<SalesInvoice> findByStatusInIgnoreCase(List<String> statuses);
}