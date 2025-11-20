package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.PurchaseOrderRequest;
import org.nishgrid.clienterp.model.PurchaseOrder;
import org.nishgrid.clienterp.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrderRequest request) {
        PurchaseOrder savedPo = purchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPo);
    }

    @GetMapping
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderService.getAllPurchaseOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable("id") Long id) {
        PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(po);
    }

    // ADD THIS MISSING METHOD
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrder(@PathVariable("id") Long id, @RequestBody PurchaseOrderRequest request) {
        PurchaseOrder updatedPo = purchaseOrderService.updatePurchaseOrder(id, request);
        return ResponseEntity.ok(updatedPo);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrder> updateOrderStatus(@PathVariable("id") Long id, @RequestBody StatusUpdateRequest request) {
        PurchaseOrder updatedPo = purchaseOrderService.updatePurchaseOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedPo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable("id") Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }

    static class StatusUpdateRequest {
        private PurchaseOrder.PoStatus status;
        public PurchaseOrder.PoStatus getStatus() { return status; }
        public void setStatus(PurchaseOrder.PoStatus status) { this.status = status; }
    }
}