package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.PayslipRequest;
import org.nishgrid.clienterp.exception.DuplicateResourceException;
import org.nishgrid.clienterp.model.Payslip;
import org.nishgrid.clienterp.service.PayslipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
@RequiredArgsConstructor
public class PayslipController {

    private final PayslipService service;

    @PostMapping
    public ResponseEntity<?> generatePayslip(@RequestBody PayslipRequest request) {
        try {
            String message = service.generatePayslip(request);
            return ResponseEntity.ok().body(message);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/calculate")
    public ResponseEntity<PayslipRequest> getCalculatedPayslip(
            @RequestParam("employeeCode") String employeeCode,
            @RequestParam("month") String month) {
        PayslipRequest calculatedData = service.calculatePayslipDetails(employeeCode, month);
        return ResponseEntity.ok(calculatedData);
    }

    @GetMapping
    public ResponseEntity<List<Payslip>> getAllPayslips() {
        return ResponseEntity.ok(service.getAllPayslips());
    }

    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<List<Payslip>> getPayslipsByEmployee(@PathVariable("employeeCode") String employeeCode) {
        return ResponseEntity.ok(service.getPayslipsByEmployee(employeeCode));
    }

    @GetMapping("/employee/{employeeCode}/month/{month}")
    public ResponseEntity<Payslip> getPayslipByEmployeeAndMonth(
            @PathVariable("employeeCode") String employeeCode,
            @PathVariable("month") String month) {
        return ResponseEntity.ok(service.getPayslipByEmployeeAndMonth(employeeCode, month));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payslip>> getPayslipsByPaymentStatus(
            @PathVariable("status") String status) {
        return ResponseEntity.ok(service.getPayslipsByPaymentStatus(status));
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<Payslip>> getPayslipsByMonth(
            @PathVariable("month") String month) {
        return ResponseEntity.ok(service.getPayslipsByMonth(month));
    }

    @PutMapping("/{payslipId}/status")
    public ResponseEntity<String> updatePaymentStatus(
            @PathVariable("payslipId") Integer payslipId,
            @RequestParam("status") String status) {
        return ResponseEntity.ok(service.updatePaymentStatus(payslipId, status));
    }
}