package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.PurchaseReturnRequest;
import org.nishgrid.clienterp.dto.PurchaseReturnResponse;
import org.nishgrid.clienterp.model.PurchaseInvoice;
import org.nishgrid.clienterp.model.PurchaseReturn;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.repository.PurchaseInvoiceRepository;
import org.nishgrid.clienterp.repository.PurchaseReturnRepository;
import org.nishgrid.clienterp.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseReturnServiceImpl implements PurchaseReturnService {

    @Autowired private PurchaseReturnRepository returnRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private PurchaseInvoiceRepository invoiceRepository;

    @Override
    public PurchaseReturnResponse createReturn(PurchaseReturnRequest request) {
        PurchaseReturn purchaseReturn = new PurchaseReturn();
        mapDtoToEntity(request, purchaseReturn);
        PurchaseReturn savedReturn = returnRepository.save(purchaseReturn);
        return PurchaseReturnResponse.fromEntity(savedReturn);
    }

    @Override
    public List<PurchaseReturnResponse> getAllReturns() {
        return returnRepository.findAllWithDetails().stream()
                .map(PurchaseReturnResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseReturnResponse getReturnById(Long id) {
        return returnRepository.findById(id)
                .map(PurchaseReturnResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return not found"));
    }

    @Override
    public PurchaseReturnResponse updateReturn(Long id, PurchaseReturnRequest request) {
        PurchaseReturn purchaseReturn = returnRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return not found"));
        mapDtoToEntity(request, purchaseReturn);
        PurchaseReturn updatedReturn = returnRepository.save(purchaseReturn);
        return PurchaseReturnResponse.fromEntity(updatedReturn);
    }

    @Override
    public void deleteReturn(Long id) {
        if (!returnRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Return not found");
        }
        returnRepository.deleteById(id);
    }

    private void mapDtoToEntity(PurchaseReturnRequest request, PurchaseReturn purchaseReturn) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        PurchaseInvoice invoice = invoiceRepository.findById(request.getPurchaseInvoiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Invoice not found"));

        purchaseReturn.setReturnNumber(request.getReturnNumber());
        purchaseReturn.setVendor(vendor);
        purchaseReturn.setPurchaseInvoice(invoice);
        purchaseReturn.setReturnDate(request.getReturnDate());
        purchaseReturn.setReason(request.getReason());
        purchaseReturn.setAmountReturned(request.getAmountReturned());
    }
}