package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.PurchaseOrderRequest;
import org.nishgrid.clienterp.model.PurchaseOrder;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrder createPurchaseOrder(PurchaseOrderRequest request);
    List<PurchaseOrder> getAllPurchaseOrders();
    PurchaseOrder getPurchaseOrderById(Long id);
    PurchaseOrder updatePurchaseOrderStatus(Long id, PurchaseOrder.PoStatus status);
    void deletePurchaseOrder(Long id);
    PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrderRequest request);
}