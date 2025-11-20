package org.nishgrid.clienterp.controller;

import jakarta.validation.ValidationException;
import org.nishgrid.clienterp.dto.OldGoldExchangeRequestDTO;
import org.nishgrid.clienterp.model.OldGoldExchange;
import org.nishgrid.clienterp.service.OldGoldExchangeService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/old-gold-exchange")
public class OldGoldExchangeController {

    private final OldGoldExchangeService exchangeService;

    public OldGoldExchangeController(OldGoldExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/next-bill-number")
    public Map<String, String> getNextBillNumber() {
        String nextNumber = exchangeService.getNextAvailableBillNumber();
        return Collections.singletonMap("purchaseBillNo", nextNumber);
    }

    @PostMapping
    public ResponseEntity<?> createPurchaseExchange(@RequestBody OldGoldExchangeRequestDTO request) {
        try {
            OldGoldExchangeRequestDTO completedRequest = exchangeService.processExchange(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "Success",
                    "purchaseBillNo", completedRequest.getPurchaseBillNo(),
                    "totalPurchaseValue", completedRequest.getTotalPurchaseValue(),
                    "processingFeeAmount", completedRequest.getProcessingFeeAmount(),
                    "netPayableAmount", completedRequest.getNetPayableAmount()
            ));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "Error",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "Error",
                    "message", "An unexpected server error occurred."
            ));
        }
    }

    @GetMapping
    public List<OldGoldExchangeRequestDTO> getPurchasesByFilter(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "status", required = false) String status) {

        List<OldGoldExchange> entities = exchangeService.getPurchasesByDateRangeAndStatus(startDate, endDate, status);

        return entities.stream()
                .map(this::mapEntityToRequestDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{billNo}")
    public ResponseEntity<OldGoldExchange> getPurchaseDetails(@PathVariable("billNo") String billNo) {
        return exchangeService.getPurchaseDetailByBillNo(billNo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/{billNo}/payments")
    public ResponseEntity<?> recordPurchasePayment(@PathVariable("billNo") String billNo, @RequestBody Map<String, BigDecimal> paymentData) {
        try {
            BigDecimal amountPaid = paymentData.get("amountPaid");
            if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid payment amount."));
            }

            OldGoldExchange updatedEntity = exchangeService.updatePaymentStatus(billNo, amountPaid);

            return ResponseEntity.ok(Map.of(
                    "status", "Success",
                    "message", "Payment recorded. New status: " + updatedEntity.getStatus()
            ));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to record payment."));
        }
    }

    private OldGoldExchangeRequestDTO mapEntityToRequestDTO(OldGoldExchange entity) {
        OldGoldExchangeRequestDTO dto = new OldGoldExchangeRequestDTO();
        dto.setPurchaseBillNo(entity.getPurchaseBillNo());
        dto.setPurchaseDate(entity.getPurchaseDate());
        dto.setSellerName(entity.getSellerName());
        dto.setSellerMobile(entity.getSellerMobile());
        dto.setNetPayableAmount(entity.getNetPayableAmount());
        dto.setTotalPurchaseValue(entity.getTotalPurchaseValue());
        dto.setStatus(entity.getStatus());

        return dto;
    }
}