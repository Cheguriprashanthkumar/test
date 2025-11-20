package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.ReturnRecordDTO;
import org.nishgrid.clienterp.dto.ReturnRequestDTO;
import org.nishgrid.clienterp.dto.SalesReturnTotalAmountDto;
import org.nishgrid.clienterp.model.SalesReturn;
import org.nishgrid.clienterp.repository.SalesReturnRepository;
import org.nishgrid.clienterp.service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    @Autowired
    private ReturnService returnService;

    @Autowired
    private SalesReturnRepository salesReturnRepository;

    @PostMapping
    public ResponseEntity<SalesReturn> createReturn(@RequestBody ReturnRequestDTO returnRequestDTO) {
        SalesReturn createdReturn = returnService.processReturn(returnRequestDTO);
        return new ResponseEntity<>(createdReturn, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReturnRecordDTO>> getAllReturns() {
        List<ReturnRecordDTO> returnRecords = returnService.findAllReturns();
        return ResponseEntity.ok(returnRecords);
    }

    @GetMapping("/total-amount")
    public ResponseEntity<BigDecimal> getTotalReturnedAmount(@RequestParam("invoiceId") Long invoiceId) {
        Optional<BigDecimal> totalReturnedAmount = salesReturnRepository.findTotalReturnedAmountByInvoiceId(invoiceId);
        return ResponseEntity.ok(totalReturnedAmount.orElse(BigDecimal.ZERO));
    }
    @GetMapping("/total-amount-by-invoice/{invoiceId}")
    public ResponseEntity<SalesReturnTotalAmountDto> getTotalReturnedAmountByInvoice(
            @PathVariable("invoiceId") Long invoiceId) {

        Optional<BigDecimal> totalAmountOptional = salesReturnRepository.findTotalReturnedAmountByInvoiceId(invoiceId);

        BigDecimal totalAmount = totalAmountOptional.orElse(BigDecimal.ZERO);

        SalesReturnTotalAmountDto responseDto = new SalesReturnTotalAmountDto(totalAmount);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/total-amount/period")
    public ResponseEntity<BigDecimal> getTotalReturnedAmountForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BigDecimal totalAmount = returnService.findTotalReturnedAmountByDateRange(startDate, endDate);
        return ResponseEntity.ok(totalAmount);
    }
    @PostMapping("/batch")
    public ResponseEntity<List<SalesReturn>> createReturns(@RequestBody List<ReturnRequestDTO> returnRequestDTOs) {
        List<SalesReturn> createdReturns = returnService.processReturns(returnRequestDTOs);
        return new ResponseEntity<>(createdReturns, HttpStatus.CREATED);
    }
}