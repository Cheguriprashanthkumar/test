package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.GoodsReceiptNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface GrnRepository extends JpaRepository<GoodsReceiptNote, Long> {

    @Query("SELECT DISTINCT grn FROM GoodsReceiptNote grn JOIN FETCH grn.purchaseOrder po JOIN FETCH po.vendor LEFT JOIN FETCH po.items ORDER BY grn.id DESC")
    List<GoodsReceiptNote> findAllWithDetails();

    @Query("SELECT grn FROM GoodsReceiptNote grn JOIN FETCH grn.purchaseOrder po JOIN FETCH po.vendor LEFT JOIN FETCH po.items WHERE grn.id = :id")
    Optional<GoodsReceiptNote> findByIdWithDetails(@Param("id") Long id);
}