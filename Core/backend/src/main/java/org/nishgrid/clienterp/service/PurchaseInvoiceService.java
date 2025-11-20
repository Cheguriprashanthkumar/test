package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.PurchaseInvoiceRequest;
import org.nishgrid.clienterp.dto.PurchaseInvoiceResponse;
import java.util.List;

public interface PurchaseInvoiceService {
    PurchaseInvoiceResponse createInvoice(PurchaseInvoiceRequest request);
    List<PurchaseInvoiceResponse> getAllInvoices();
    PurchaseInvoiceResponse getInvoiceById(Long id);
    PurchaseInvoiceResponse updateInvoice(Long id, PurchaseInvoiceRequest request);
    void deleteInvoice(Long id);
}