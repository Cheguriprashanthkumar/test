package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.DebitNoteRequest;
import org.nishgrid.clienterp.dto.DebitNoteResponse;
import org.nishgrid.clienterp.model.*;
import org.nishgrid.clienterp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode; // Make sure this is imported
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DebitNoteServiceImpl implements DebitNoteService {

    @Autowired private DebitNoteRepository debitNoteRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private PurchaseInvoiceRepository invoiceRepository;
    @Autowired private ProductCatalogRepository productCatalogRepository;
    @Autowired private DebitNoteAuditLogService auditLogService;

    @Value("${app.company.state}")
    private String companyState;

    @Override
    public DebitNoteResponse createDebitNote(DebitNoteRequest request) {
        DebitNote debitNote = new DebitNote();
        mapDtoToEntity(request, debitNote);
        calculateTotalsAndTaxes(debitNote);
        DebitNote savedDebitNote = debitNoteRepository.save(debitNote);
        auditLogService.logCreation(savedDebitNote, request.getCreatedBy());
        return DebitNoteResponse.fromEntity(savedDebitNote);
    }

    @Override
    public DebitNoteResponse updateDebitNote(Long id, DebitNoteRequest request) {
        DebitNote debitNote = debitNoteRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit Note not found with id: " + id));
        DebitNoteResponse oldState = DebitNoteResponse.fromEntity(debitNote);
        mapDtoToEntity(request, debitNote);
        calculateTotalsAndTaxes(debitNote);
        DebitNote updatedDebitNote = debitNoteRepository.save(debitNote);
        auditLogService.logUpdate(oldState, updatedDebitNote, request.getCreatedBy());
        return DebitNoteResponse.fromEntity(updatedDebitNote);
    }

    @Override
    public List<DebitNoteResponse> getAllDebitNotes() {
        return debitNoteRepository.findAllWithDetails().stream()
                .map(DebitNoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public DebitNoteResponse getDebitNoteById(Long id) {
        return debitNoteRepository.findByIdWithDetails(id)
                .map(DebitNoteResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit Note not found with id: " + id));
    }

    @Override
    public void deleteDebitNote(Long id) {
        if (!debitNoteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit Note not found with id: " + id);
        }
        debitNoteRepository.deleteById(id);
    }

    private void mapDtoToEntity(DebitNoteRequest request, DebitNote debitNote) {
        debitNote.setDebitNoteNo(request.getDebitNoteNo());
        debitNote.setDebitNoteDate(request.getDebitNoteDate());
        debitNote.setReason(request.getReason());
        debitNote.setStatus(request.getStatus());
        debitNote.setCreatedBy(request.getCreatedBy());
        debitNote.setApprovedBy(request.getApprovedBy());

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));
        debitNote.setVendor(vendor);

        if (request.getPurchaseInvoiceId() != null) {
            PurchaseInvoice invoice = invoiceRepository.findById(request.getPurchaseInvoiceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Invoice not found"));
            debitNote.setPurchaseInvoice(invoice);
        }

        debitNote.getItems().clear();
        if (request.getItems() != null) {
            request.getItems().forEach(itemDto -> {
                ProductCatalog product = productCatalogRepository.findById(itemDto.getItemId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + itemDto.getItemId() + " not found"));
                DebitNoteItem item = new DebitNoteItem();
                item.setItem(product);
                item.setHsnCode(itemDto.getHsnCode());
                item.setPurity(itemDto.getPurity());
                item.setWeight(itemDto.getWeight());
                item.setQty(itemDto.getQty());
                item.setUnitRate(itemDto.getUnitRate());
                item.setGstRate(itemDto.getGstRate());
                debitNote.addItem(item);
            });
        }
    }

    private void calculateTotalsAndTaxes(DebitNote debitNote) {
        Map<String, DebitNoteTax> taxMap = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;

        String vendorState = debitNote.getVendor().getState();
        boolean isInterState = vendorState != null && !vendorState.equalsIgnoreCase(companyState);

        for (DebitNoteItem item : debitNote.getItems()) {
            BigDecimal qty = (item.getQty() != null) ? new BigDecimal(item.getQty()) : BigDecimal.ONE;
            BigDecimal lineTotal = item.getUnitRate().multiply(qty);
            BigDecimal gstAmount = BigDecimal.ZERO;

            if (item.getGstRate() != null && item.getGstRate().compareTo(BigDecimal.ZERO) > 0) {
                gstAmount = lineTotal.multiply(item.getGstRate().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));

                if (isInterState) {
                    updateTaxMap("IGST", item.getGstRate(), gstAmount, taxMap);
                } else {
                    BigDecimal halfGstRate = item.getGstRate().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
                    BigDecimal halfGstAmount = gstAmount.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
                    updateTaxMap("CGST", halfGstRate, halfGstAmount, taxMap);
                    updateTaxMap("SGST", halfGstRate, gstAmount.subtract(halfGstAmount), taxMap);
                }
            }

            item.setLineTotal(lineTotal.setScale(2, RoundingMode.HALF_UP));
            item.setGstAmount(gstAmount.setScale(2, RoundingMode.HALF_UP));
            item.setTotalWithGst(lineTotal.add(gstAmount).setScale(2, RoundingMode.HALF_UP));

            totalAmount = totalAmount.add(item.getLineTotal());
            totalGst = totalGst.add(item.getGstAmount());
        }

        debitNote.getTaxes().clear();
        taxMap.values().forEach(debitNote::addTax);

        debitNote.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
        debitNote.setTotalGstAmount(totalGst.setScale(2, RoundingMode.HALF_UP));
        // --- THIS LINE IS NOW FIXED ---
        debitNote.setTotalAmountWithGst(totalAmount.add(totalGst).setScale(2, RoundingMode.HALF_UP));
    }

    private void updateTaxMap(String type, BigDecimal rate, BigDecimal amount, Map<String, DebitNoteTax> taxMap) {
        String key = type + "@" + rate;
        DebitNoteTax tax = taxMap.get(key);
        if (tax == null) {
            tax = new DebitNoteTax();
            tax.setTaxType(DebitNoteTax.TaxType.valueOf(type));
            tax.setTaxRate(rate);
            tax.setTaxAmount(amount);
            taxMap.put(key, tax);
        } else {
            tax.setTaxAmount(tax.getTaxAmount().add(amount));
        }
    }
}