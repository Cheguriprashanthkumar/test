package org.nishgrid.clienterp.repository;


import org.nishgrid.clienterp.model.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesItemRepository extends JpaRepository<SalesItem, Long> {


    List<SalesItem> findByInvoice_InvoiceId(Long invoiceId);
}
