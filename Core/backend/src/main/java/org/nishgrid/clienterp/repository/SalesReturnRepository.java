package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.SalesReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SalesReturnRepository extends JpaRepository<SalesReturn, Long> {

    @Query("SELECT SUM(sr.quantity) FROM SalesReturn sr WHERE sr.salesItem.salesItemId = :salesItemId")
    Optional<BigDecimal> findTotalReturnedQuantityBySalesItemId(@Param("salesItemId") Long salesItemId);

    boolean existsBySalesInvoice_InvoiceId(Long invoiceId);

    @Query("SELECT COALESCE(SUM(sr.returnAmount), 0) FROM SalesReturn sr WHERE sr.salesInvoice.invoiceId = :invoiceId")
    Optional<BigDecimal> findTotalReturnedAmountByInvoiceId(@Param("invoiceId") Long invoiceId);

    @Query("SELECT COALESCE(SUM(sr.returnAmount), 0) FROM SalesReturn sr WHERE sr.returnDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> findTotalReturnedAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}