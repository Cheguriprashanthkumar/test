package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.PurchaseInvoiceRequest;
import org.nishgrid.clienterp.dto.PurchaseInvoiceResponse;
import org.nishgrid.clienterp.model.PurchaseInvoice;
import org.nishgrid.clienterp.model.PurchaseOrder;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.repository.PurchaseInvoiceRepository;
import org.nishgrid.clienterp.repository.PurchaseOrderRepository;
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
public class PurchaseInvoiceServiceImpl implements PurchaseInvoiceService {

    @Autowired private PurchaseInvoiceRepository invoiceRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public PurchaseInvoiceResponse createInvoice(PurchaseInvoiceRequest request) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        PurchaseOrder po = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found"));

        PurchaseInvoice invoice = new PurchaseInvoice();
        mapDtoToEntity(request, vendor, po, invoice);

        PurchaseInvoice savedInvoice = invoiceRepository.save(invoice);
        return PurchaseInvoiceResponse.fromEntity(savedInvoice);
    }

    @Override
    public List<PurchaseInvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(PurchaseInvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseInvoiceResponse getInvoiceById(Long id) {
        PurchaseInvoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return PurchaseInvoiceResponse.fromEntity(invoice);
    }

    @Override
    public PurchaseInvoiceResponse updateInvoice(Long id, PurchaseInvoiceRequest request) {
        PurchaseInvoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        PurchaseOrder po = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found"));

        mapDtoToEntity(request, vendor, po, invoice);

        PurchaseInvoice updatedInvoice = invoiceRepository.save(invoice);
        return PurchaseInvoiceResponse.fromEntity(updatedInvoice);
    }

    @Override
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
        }
        invoiceRepository.deleteById(id);
    }

    private void mapDtoToEntity(PurchaseInvoiceRequest request, Vendor vendor, PurchaseOrder po, PurchaseInvoice invoice) {
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setVendor(vendor);
        invoice.setPurchaseOrder(po);
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setGstAmount(request.getGstAmount());
        invoice.setGrandTotal(request.getGrandTotal());
    }
}