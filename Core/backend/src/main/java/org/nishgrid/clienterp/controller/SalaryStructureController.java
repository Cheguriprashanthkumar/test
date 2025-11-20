package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.SalaryStructureRequest;
import org.nishgrid.clienterp.model.SalaryStructure;
import org.nishgrid.clienterp.service.SalaryStructureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
public class SalaryStructureController {

    private final SalaryStructureService salaryStructureService;

    @PostMapping
    public ResponseEntity<?> createSalaryStructure(@RequestBody SalaryStructureRequest request) {
        boolean success = salaryStructureService.createSalaryStructure(request);

        if (success) {
            return ResponseEntity.ok("Salary structure saved successfully.");
        } else {
            return ResponseEntity.status(404).body("Employee not found.");
        }
    }

    @GetMapping
    public List<SalaryStructure> getAllSalaryStructures() {
        return salaryStructureService.getAllSalaryStructures();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSalaryStructureById(@PathVariable("id") Integer id) {
        SalaryStructure salaryStructure = salaryStructureService.getById(id);
        if (salaryStructure != null) {
            return ResponseEntity.ok(salaryStructure);
        } else {
            return ResponseEntity.status(404).body("Salary structure not found.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSalaryStructure(
            @PathVariable("id") Integer id,
            @RequestBody SalaryStructureRequest request) {
        boolean updated = salaryStructureService.updateSalaryStructure(id, request);
        if (updated) {
            return ResponseEntity.ok("Salary structure updated successfully.");
        } else {
            return ResponseEntity.status(404).body("Salary structure not found.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSalaryStructure(@PathVariable("id") Integer id) {
        boolean deleted = salaryStructureService.deleteSalaryStructure(id);
        if (deleted) {
            return ResponseEntity.ok("Salary structure deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Salary structure not found.");
        }
    }
}
