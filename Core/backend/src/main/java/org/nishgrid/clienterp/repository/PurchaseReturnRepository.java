package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.PurchaseReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PurchaseReturnRepository extends JpaRepository<PurchaseReturn, Long> {

    @Query("SELECT pr FROM PurchaseReturn pr JOIN FETCH pr.vendor JOIN FETCH pr.purchaseInvoice ORDER BY pr.id DESC")
    List<PurchaseReturn> findAllWithDetails();
}