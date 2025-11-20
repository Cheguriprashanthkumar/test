package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.PurchaseOrderRequest;
import org.nishgrid.clienterp.model.PurchaseOrder;
import org.nishgrid.clienterp.model.PurchaseOrderItem;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.repository.PurchaseOrderRepository;
import org.nishgrid.clienterp.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private AuditLogService auditLogService;

    @Override
    public PurchaseOrder createPurchaseOrder(PurchaseOrderRequest request) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found with id: " + request.getVendorId()));

        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(request.getPoNumber());
        po.setVendor(vendor);
        po.setOrderDate(request.getOrderDate());
        po.setRemarks(request.getRemarks());
        po.setTotalAmount(request.getTotalAmount());
        po.setStatus(PurchaseOrder.PoStatus.PENDING);

        request.getItems().forEach(itemDto -> {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProductName(itemDto.getProductName());
            item.setPurity(itemDto.getPurity());
            item.setWeight(itemDto.getWeight());
            item.setRatePerUnit(itemDto.getRatePerUnit());
            item.setTaxPercent(itemDto.getTaxPercent());
            item.setTotalPrice(itemDto.getTotalPrice());
            po.addItem(item);
        });

        PurchaseOrder savedPo = purchaseOrderRepository.save(po);

        String details = "Created Purchase Order: " + savedPo.getPoNumber();
        auditLogService.logAction("admin", "CREATE", "Purchase Order", details);
        auditLogService.logStatusChange(savedPo, "N/A", savedPo.getStatus().toString(), "admin");

        return savedPo;
    }

    @Override
    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrderRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found with id: " + id));

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found with id: " + request.getVendorId()));

        po.setPoNumber(request.getPoNumber());
        po.setVendor(vendor);
        po.setOrderDate(request.getOrderDate());
        po.setRemarks(request.getRemarks());
        po.setTotalAmount(request.getTotalAmount());

        po.getItems().clear();
        request.getItems().forEach(itemDto -> {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProductName(itemDto.getProductName());
            item.setPurity(itemDto.getPurity());
            item.setWeight(itemDto.getWeight());
            item.setRatePerUnit(itemDto.getRatePerUnit());
            item.setTaxPercent(itemDto.getTaxPercent());
            item.setTotalPrice(itemDto.getTotalPrice());
            po.addItem(item);
        });

        PurchaseOrder updatedPo = purchaseOrderRepository.save(po);

        String details = "Updated Purchase Order: " + updatedPo.getPoNumber();
        auditLogService.logAction("admin", "UPDATE", "Purchase Order", details);

        return updatedPo;
    }

    @Override
    public PurchaseOrder updatePurchaseOrderStatus(Long id, PurchaseOrder.PoStatus status) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found with id: " + id));

        String oldStatus = po.getStatus().toString();
        po.setStatus(status);

        PurchaseOrder updatedPo = purchaseOrderRepository.save(po);

        auditLogService.logStatusChange(updatedPo, oldStatus, status.toString(), "admin");

        return updatedPo;
    }

    @Override
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found with id: " + id));

        String details = "Deleted Purchase Order: " + po.getPoNumber();
        auditLogService.logAction("admin", "DELETE", "Purchase Order", details);

        purchaseOrderRepository.deleteById(id);
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAllWithDetails();
    }

    @Override
    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found with id: " + id));
    }
}