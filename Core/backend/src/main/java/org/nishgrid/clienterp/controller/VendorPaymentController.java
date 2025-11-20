package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.VendorPaymentRequest;
import org.nishgrid.clienterp.dto.VendorPaymentResponse;
import org.nishgrid.clienterp.service.VendorPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-payments")
public class VendorPaymentController {

    @Autowired
    private VendorPaymentService paymentService;

    @PostMapping
    public ResponseEntity<VendorPaymentResponse> createPayment(@RequestBody VendorPaymentRequest request) {
        VendorPaymentResponse createdPayment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @GetMapping
    public List<VendorPaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorPaymentResponse> getPaymentById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorPaymentResponse> updatePayment(@PathVariable("id") Long id, @RequestBody VendorPaymentRequest request) {
        return ResponseEntity.ok(paymentService.updatePayment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable("id") Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}