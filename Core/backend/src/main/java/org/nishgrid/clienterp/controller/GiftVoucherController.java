package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.GiftVoucherCreateDTO;
import org.nishgrid.clienterp.dto.GiftVoucherResponseDTO;
import org.nishgrid.clienterp.dto.VoucherStatusUpdateDTO;
import org.nishgrid.clienterp.service.GiftVoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "*")
public class GiftVoucherController {

    private final GiftVoucherService voucherService;

    public GiftVoucherController(GiftVoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<List<GiftVoucherResponseDTO>> getAllVouchers() {
        List<GiftVoucherResponseDTO> vouchers = voucherService.findAllVouchers().stream()
                .map(GiftVoucherResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vouchers);
    }

    @PostMapping
    public ResponseEntity<GiftVoucherResponseDTO> createVoucher(@RequestBody GiftVoucherCreateDTO createDTO) {
        GiftVoucherResponseDTO createdVoucher = GiftVoucherResponseDTO.fromEntity(
                voucherService.createVoucher(createDTO)
        );
        return new ResponseEntity<>(createdVoucher, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<GiftVoucherResponseDTO> updateStatus(@PathVariable("id") Long id, @RequestBody VoucherStatusUpdateDTO statusUpdateDTO) {
        GiftVoucherResponseDTO updatedVoucher = GiftVoucherResponseDTO.fromEntity(
                voucherService.updateVoucherStatus(id, statusUpdateDTO.getStatus())
        );
        return ResponseEntity.ok(updatedVoucher);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable("id") Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }
}