package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.BankDetailsRequest;
import org.nishgrid.clienterp.dto.BankDetailsResponse;
import org.nishgrid.clienterp.service.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/bank-details")
public class BankDetailsController {

    @Autowired
    private BankDetailsService bankDetailsService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BankDetailsResponse> createBankDetails(@RequestPart("details") BankDetailsRequest request,
                                                                 @RequestPart(value = "qrCode", required = false) MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bankDetailsService.createBankDetails(request, file));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BankDetailsResponse> updateBankDetails(@PathVariable("id") Long id,
                                                                 @RequestPart("details") BankDetailsRequest request,
                                                                 @RequestPart(value = "qrCode", required = false) MultipartFile file) {
        return ResponseEntity.ok(bankDetailsService.updateBankDetails(id, request, file));
    }

    @GetMapping
    public List<BankDetailsResponse> getAllBankDetails() {
        return bankDetailsService.getAllBankDetails();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankDetailsResponse> getBankDetailsById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bankDetailsService.getBankDetailsById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankDetails(@PathVariable("id") Long id) {
        bankDetailsService.deleteBankDetails(id);
        return ResponseEntity.noContent().build();
    }
}