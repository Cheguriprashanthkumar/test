package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {}