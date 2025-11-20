package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.PurchaseDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseDocumentRepository extends JpaRepository<PurchaseDocument, Long> {
    List<PurchaseDocument> findByPurchaseInvoiceId(Long purchaseInvoiceId);
}