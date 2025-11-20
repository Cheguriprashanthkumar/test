package org.nishgrid.clienterp.service;

import jakarta.persistence.EntityNotFoundException;
import org.nishgrid.clienterp.dto.PaymentRequestDTO;
import org.nishgrid.clienterp.event.SaleAuditEvent;
import org.nishgrid.clienterp.model.PaymentTransaction;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private SalesInvoiceRepository salesInvoiceRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static final int STANDARD_SCALE = 2;
    private static final RoundingMode STANDARD_ROUNDING = RoundingMode.HALF_UP;

    @Transactional
    public SalesInvoice addPayment(Long invoiceId, PaymentRequestDTO paymentDto) {
        SalesInvoice invoice = salesInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + invoiceId));

        String oldStatus = invoice.getStatus();

        if ("PAID".equalsIgnoreCase(oldStatus) || "CANCELLED".equalsIgnoreCase(oldStatus)) {
            throw new IllegalStateException("Invoice is already fully settled or cancelled.");
        }

        BigDecimal paymentAmount = Optional.ofNullable(paymentDto.getAmount()).orElse(BigDecimal.ZERO);
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive.");
        }

        BigDecimal netAmount = Optional.ofNullable(invoice.getNetAmount()).orElse(BigDecimal.ZERO);
        BigDecimal oldGoldValue = Optional.ofNullable(invoice.getOldGoldValue()).orElse(BigDecimal.ZERO);

        BigDecimal totalPayable = netAmount.subtract(oldGoldValue);

        BigDecimal paidAmount = Optional.ofNullable(invoice.getPaidAmount()).orElse(BigDecimal.ZERO);
        BigDecimal remainingDue = totalPayable.subtract(paidAmount);

        if (remainingDue.compareTo(BigDecimal.ZERO) < 0) {
            remainingDue = BigDecimal.ZERO;
        }

        if (paymentAmount.compareTo(remainingDue) > 0) {
            throw new IllegalArgumentException("Payment of " + paymentAmount + " exceeds the remaining due amount of " + remainingDue);
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setAmount(paymentAmount);
        transaction.setPaymentDate(Optional.ofNullable(paymentDto.getPaymentDate()).orElse(LocalDate.now()));
        transaction.setPaymentMode(paymentDto.getMode());
        transaction.setReferenceNo(paymentDto.getReferenceNo());
        transaction.setReceivedBy(paymentDto.getReceivedBy());

        invoice.addPaymentTransaction(transaction);

        BigDecimal newPaidAmount = paidAmount.add(paymentAmount);
        invoice.setPaidAmount(newPaidAmount.setScale(STANDARD_SCALE, STANDARD_ROUNDING));

        BigDecimal newDueAmount = totalPayable.subtract(newPaidAmount);
        if (newDueAmount.compareTo(BigDecimal.ZERO) < 0) {
            newDueAmount = BigDecimal.ZERO;
        }
        invoice.setDueAmount(newDueAmount.setScale(STANDARD_SCALE, STANDARD_ROUNDING));

        if (invoice.getDueAmount().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIALLY PAID");
        }

        SalesInvoice savedInvoice = salesInvoiceRepository.save(invoice);

        eventPublisher.publishEvent(new SaleAuditEvent("UPDATED", Map.of("status", oldStatus), savedInvoice));

        return savedInvoice;
    }
}