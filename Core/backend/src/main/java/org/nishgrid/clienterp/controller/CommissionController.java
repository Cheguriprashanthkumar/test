package org.nishgrid.clienterp.controller;

import jakarta.validation.Valid;
import org.nishgrid.clienterp.dto.SalesCommissionDTO;
import org.nishgrid.clienterp.model.SalesCommission;
import org.nishgrid.clienterp.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/commissions")
public class CommissionController {

    @Autowired private CommissionService commissionService;

    @GetMapping
    public List<SalesCommissionDTO> getAllCommissions() {
        return commissionService.getAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @PostMapping
    public SalesCommissionDTO createCommission(@Valid @RequestBody SalesCommissionDTO dto) {
        return convertToDto(commissionService.create(dto));
    }

    @PutMapping("/{id}")
    public SalesCommissionDTO updateCommission(@PathVariable("id") Long id, @Valid @RequestBody SalesCommissionDTO dto) {
        return convertToDto(commissionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommission(@PathVariable("id") Long id) {
        commissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private SalesCommissionDTO convertToDto(SalesCommission commission) {
        SalesCommissionDTO dto = new SalesCommissionDTO();
        dto.setCommissionId(commission.getCommissionId());
        dto.setCommissionRate(commission.getCommissionRate());
        dto.setAmountTotal(commission.getAmountTotal());
        if (commission.getEmployee() != null) {
            dto.setEmployeeId(commission.getEmployee().getEmployeeId());
            dto.setEmployeeName(commission.getEmployee().getFullName());
        }
        if (commission.getSalesInvoice() != null) {
            dto.setInvoiceId(commission.getSalesInvoice().getInvoiceId());
            dto.setInvoiceNo(commission.getSalesInvoice().getInvoiceNo());
        }
        return dto;
    }
}