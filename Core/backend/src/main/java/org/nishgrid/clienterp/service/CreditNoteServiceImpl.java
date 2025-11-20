package org.nishgrid.clienterp.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.CreditNotePaymentRequest;
import org.nishgrid.clienterp.dto.CreditNoteRequest;
import org.nishgrid.clienterp.dto.CreditNoteResponse;
import org.nishgrid.clienterp.model.*;
import org.nishgrid.clienterp.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CreditNoteServiceImpl implements CreditNoteService {

    private final CreditNoteRepository creditNoteRepository;
    private final CustomerRepository customerRepository;
    private final SalesInvoiceRepository salesInvoiceRepository;
    private final DocumentSequenceService sequenceService;
    private final ProductCatalogRepository productRepository;
    private final CreditNoteAuditLogService auditLogService;

    @Value("${app.company.state}")
    private String companyState;

    @Override
    public CreditNoteResponse createCreditNote(CreditNoteRequest request) {
        CreditNote creditNote = new CreditNote();
        creditNote.setCreditNoteNumber(sequenceService.getNextCreditNoteNumber());
        mapRequestToEntity(request, creditNote);
        calculateTotalsAndTaxes(creditNote);

        CreditNote savedNote = creditNoteRepository.save(creditNote);
        auditLogService.logCreation(savedNote, request.getIssuedBy());
        return CreditNoteResponse.fromEntity(savedNote);
    }

    @Override
    public CreditNoteResponse updateCreditNote(Long id, CreditNoteRequest request) {
        CreditNote creditNote = creditNoteRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Note not found with id: " + id));

        CreditNote oldState = new CreditNote();
        oldState.setStatus(creditNote.getStatus());

        mapRequestToEntity(request, creditNote);
        calculateTotalsAndTaxes(creditNote);

        CreditNote updatedNote = creditNoteRepository.save(creditNote);
        auditLogService.logUpdate(oldState, updatedNote, request.getIssuedBy());
        return CreditNoteResponse.fromEntity(updatedNote);
    }

    @Override
    public CreditNoteResponse getCreditNoteById(Long id) {
        return creditNoteRepository.findByIdWithDetails(id)
                .map(CreditNoteResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Note not found with id: " + id));
    }

    @Override
    public List<CreditNoteResponse> getAllCreditNotes() {
        return creditNoteRepository.findAllWithDetails().stream()
                .map(CreditNoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCreditNote(Long id) {
        CreditNote noteToDelete = creditNoteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Note not found with id: " + id));

        auditLogService.logDeletion(noteToDelete, "SYSTEM_ADMIN");
        creditNoteRepository.deleteById(id);
    }

    @Override
    public CreditNoteResponse addPayment(Long creditNoteId, @Valid CreditNotePaymentRequest paymentRequest) {
        CreditNote creditNote = creditNoteRepository.findByIdWithDetails(creditNoteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Note not found with id: " + creditNoteId));

        CreditNotePayment payment = new CreditNotePayment();
        payment.setSettlementType(paymentRequest.getSettlementType());
        payment.setAmount(paymentRequest.getAmount());
        payment.setSettlementDate(paymentRequest.getSettlementDate());
        payment.setReferenceNumber(paymentRequest.getReferenceNumber());

        creditNote.addPayment(payment);

        BigDecimal totalPaid = creditNote.getPayments().stream()
                .map(CreditNotePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(creditNote.getTotalAmountIncludingTax()) >= 0) {
            creditNote.setStatus(CreditNote.CreditNoteStatus.Settled);
        }

        CreditNote savedNote = creditNoteRepository.save(creditNote);

        String user = "SYSTEM"; // You would get the logged-in user here
        auditLogService.log(savedNote.getCreditNoteId(), user,
                "Payment of " + payment.getAmount() + " added via " + payment.getSettlementType());

        return CreditNoteResponse.fromEntity(savedNote);
    }

    private void mapRequestToEntity(CreditNoteRequest request, CreditNote creditNote) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with id: " + request.getCustomerId()));
        creditNote.setCustomer(customer);
        creditNote.setCreditNoteDate(request.getCreditNoteDate());
        creditNote.setReason(request.getReason());
        creditNote.setStatus(request.getStatus());
        creditNote.setIssuedBy(request.getIssuedBy());
        creditNote.setApprovedBy(request.getApprovedBy());
        creditNote.setRemarks(request.getRemarks());
        creditNote.setCurrency(request.getCurrency());
        if (request.getOriginalInvoiceId() != null) {
            SalesInvoice invoice = salesInvoiceRepository.findById(request.getOriginalInvoiceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sales Invoice not found with id: " + request.getOriginalInvoiceId()));
            creditNote.setOriginalInvoice(invoice);
        } else {
            creditNote.setOriginalInvoice(null);
        }

        creditNote.getItems().clear();
        if (request.getItems() != null) {
            request.getItems().forEach(itemDto -> {
                ProductCatalog product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + itemDto.getProductId()));
                CreditNoteItem item = new CreditNoteItem();
                item.setProduct(product);
                item.setDescription(itemDto.getDescription());
                item.setHsnCode(itemDto.getHsnCode());
                item.setPurity(itemDto.getPurity());
                item.setWeight(itemDto.getWeight());
                item.setQuantity(itemDto.getQuantity());
                item.setRatePerGram(itemDto.getRatePerGram());
                item.setDiscountAmount(itemDto.getDiscountAmount() != null ? itemDto.getDiscountAmount() : BigDecimal.ZERO);
                item.setTaxRate(itemDto.getTaxRate());
                creditNote.addItem(item);
            });
        }
    }

    private void calculateTotalsAndTaxes(CreditNote creditNote) {
        Map<String, CreditNoteTax> taxMap = new HashMap<>();
        BigDecimal grandTotalAmount = BigDecimal.ZERO;
        BigDecimal grandTotalTax = BigDecimal.ZERO;

        String customerState = creditNote.getCustomer().getState();
        boolean isInterState = customerState != null && !customerState.equalsIgnoreCase(companyState);

        for (CreditNoteItem item : creditNote.getItems()) {
            BigDecimal quantity = new BigDecimal(item.getQuantity());
            BigDecimal baseAmount = item.getRatePerGram().multiply(item.getWeight()).multiply(quantity);
            BigDecimal taxableAmount = baseAmount.subtract(item.getDiscountAmount());
            BigDecimal taxAmount = BigDecimal.ZERO;

            if (item.getTaxRate() != null && item.getTaxRate().compareTo(BigDecimal.ZERO) > 0) {
                taxAmount = taxableAmount.multiply(item.getTaxRate().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));

                if (isInterState) {
                    updateTaxMap("IGST", item.getTaxRate(), taxAmount, taxMap);
                } else {
                    BigDecimal halfRate = item.getTaxRate().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
                    BigDecimal halfAmount = taxAmount.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
                    updateTaxMap("CGST", halfRate, halfAmount, taxMap);
                    updateTaxMap("SGST", halfRate, taxAmount.subtract(halfAmount), taxMap);
                }
            }

            item.setTaxableAmount(taxableAmount.setScale(2, RoundingMode.HALF_UP));
            item.setTaxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP));
            item.setTotalAmount(taxableAmount.add(taxAmount).setScale(2, RoundingMode.HALF_UP));

            grandTotalAmount = grandTotalAmount.add(item.getTaxableAmount());
            grandTotalTax = grandTotalTax.add(item.getTaxAmount());
        }

        creditNote.getTaxes().clear();
        taxMap.values().forEach(creditNote::addTax);

        creditNote.setTotalAmount(grandTotalAmount.setScale(2, RoundingMode.HALF_UP));
        creditNote.setTotalTax(grandTotalTax.setScale(2, RoundingMode.HALF_UP));
        creditNote.setTotalAmountIncludingTax(grandTotalAmount.add(grandTotalTax).setScale(2, RoundingMode.HALF_UP));
    }

    private void updateTaxMap(String type, BigDecimal rate, BigDecimal amount, Map<String, CreditNoteTax> taxMap) {
        String key = type + "@" + rate;
        CreditNoteTax tax = taxMap.get(key);
        if (tax == null) {
            tax = new CreditNoteTax();
            tax.setTaxType(CreditNoteTax.TaxType.valueOf(type));
            tax.setTaxRate(rate);
            tax.setTaxAmount(amount);
            taxMap.put(key, tax);
        } else {
            tax.setTaxAmount(tax.getTaxAmount().add(amount));
        }
    }
}