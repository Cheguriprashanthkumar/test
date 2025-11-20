package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.CustomerKycRequestDTO;
import org.nishgrid.clienterp.dto.CustomerKycResponseDTO;
import org.nishgrid.clienterp.service.CustomerKycService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kyc")
@CrossOrigin(origins = "*")
public class CustomerKycController {

    private final CustomerKycService kycService;

    public CustomerKycController(CustomerKycService kycService) {
        this.kycService = kycService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerKycResponseDTO>> getAllKyc() {
        List<CustomerKycResponseDTO> kycList = kycService.findAllKyc().stream()
                .map(CustomerKycResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(kycList);
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<CustomerKycResponseDTO> createKyc(@PathVariable("customerId") Long customerId, @RequestBody CustomerKycRequestDTO dto) {
        CustomerKycResponseDTO createdKyc = CustomerKycResponseDTO.fromEntity(
                kycService.saveOrUpdateKyc(customerId, dto)
        );
        return new ResponseEntity<>(createdKyc, HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerKycResponseDTO> updateKyc(@PathVariable("customerId") Long customerId, @RequestBody CustomerKycRequestDTO dto) {
        CustomerKycResponseDTO updatedKyc = CustomerKycResponseDTO.fromEntity(
                kycService.saveOrUpdateKyc(customerId, dto)
        );
        return ResponseEntity.ok(updatedKyc);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteKyc(@PathVariable("customerId") Long customerId) {
        kycService.deleteKyc(customerId);
        return ResponseEntity.noContent().build();
    }
}