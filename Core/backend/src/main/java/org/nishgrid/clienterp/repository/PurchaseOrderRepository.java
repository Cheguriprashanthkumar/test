package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @Query("SELECT DISTINCT po FROM PurchaseOrder po JOIN FETCH po.vendor LEFT JOIN FETCH po.items ORDER BY po.id DESC")
    List<PurchaseOrder> findAllWithDetails();

    @Query("SELECT po FROM PurchaseOrder po JOIN FETCH po.vendor LEFT JOIN FETCH po.items WHERE po.id = :id")
    Optional<PurchaseOrder> findByIdWithDetails(@Param("id") Long id);
}