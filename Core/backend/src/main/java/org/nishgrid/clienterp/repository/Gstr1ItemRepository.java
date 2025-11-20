package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.dto.Gstr1HsnSummaryDTO;
import org.nishgrid.clienterp.dto.Gstr3bAggregationDTO;
import org.nishgrid.clienterp.model.Gstr1Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface Gstr1ItemRepository extends JpaRepository<Gstr1Item, Long> {

    @Query("SELECT new org.nishgrid.clienterp.dto.Gstr3bAggregationDTO(" +
            "COALESCE(SUM(g.taxableValue), 0.0), " +
            "COALESCE(SUM(g.cgstAmount), 0.0), " +
            "COALESCE(SUM(g.sgstAmount), 0.0), " +
            "COALESCE(SUM(g.igstAmount), 0.0)) " +
            "FROM Gstr1Item g WHERE g.exportMonth = :monthYear")
    Gstr3bAggregationDTO getGstr3bSummaryForMonth(@Param("monthYear") String monthYear);

    List<Gstr1Item> findByExportMonth(String monthYear);

    List<Gstr1Item> findBySalesInvoiceInvoiceId(Long id);

    @Query("SELECT new org.nishgrid.clienterp.dto.Gstr1HsnSummaryDTO(" +
            "g.itemHsn, " +
            "SUM(g.taxableValue), " +
            "SUM(g.igstAmount), " +
            "SUM(g.cgstAmount), " +
            "SUM(g.sgstAmount)) " +
            "FROM Gstr1Item g WHERE g.exportMonth = :monthYear GROUP BY g.itemHsn")
    List<Gstr1HsnSummaryDTO> getGstr1HsnSummaryForMonth(@Param("monthYear") String monthYear);

    @Transactional
    @Modifying
    void deleteBySalesInvoiceInvoiceId(Long invoiceId);
}