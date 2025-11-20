package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.GrnRequest;
import org.nishgrid.clienterp.dto.GrnResponse;
import org.nishgrid.clienterp.model.GoodsReceiptNote;
import org.nishgrid.clienterp.model.PurchaseOrder;
import org.nishgrid.clienterp.repository.GrnRepository;
import org.nishgrid.clienterp.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class GrnServiceImpl implements GrnService {

    @Autowired
    private GrnRepository grnRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public GrnResponse createGrn(GrnRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findByIdWithDetails(request.getPurchaseOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found with id: " + request.getPurchaseOrderId()));

        po.setStatus(PurchaseOrder.PoStatus.RECEIVED);

        GoodsReceiptNote grn = new GoodsReceiptNote();
        grn.setGrnNumber(request.getGrnNumber());
        grn.setPurchaseOrder(po);
        grn.setReceivedDate(request.getReceivedDate());
        grn.setReceivedBy(request.getReceivedBy());
        grn.setRemarks(request.getRemarks());

        GoodsReceiptNote savedGrn = grnRepository.save(grn);
        return GrnResponse.fromEntity(savedGrn);
    }

//    @Override
//    public List<GoodsReceiptNote> getAllGrns() {
//        return grnRepository.findAll();
//    }
    @Override
    public List<GoodsReceiptNote> getAllGrns() {
        return grnRepository.findAllWithDetails();
    }
    @Override
    public GrnResponse updateGrn(Long id, GrnRequest request) {
        GoodsReceiptNote grn = grnRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GRN not found with id: " + id));

        grn.setReceivedDate(request.getReceivedDate());
        grn.setReceivedBy(request.getReceivedBy());
        grn.setRemarks(request.getRemarks());

        GoodsReceiptNote updatedGrn = grnRepository.save(grn);
        return GrnResponse.fromEntity(updatedGrn);
    }

    @Override
    public void deleteGrn(Long id) {
        GoodsReceiptNote grn = grnRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GRN not found with id: " + id));

        // Revert the associated Purchase Order's status back to PENDING
        PurchaseOrder po = grn.getPurchaseOrder();
        po.setStatus(PurchaseOrder.PoStatus.PENDING);
        purchaseOrderRepository.save(po);

        grnRepository.deleteById(id);
    }
}