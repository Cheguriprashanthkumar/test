package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.CompanyDetails;
import org.nishgrid.clienterp.service.CompanyDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company-details")
public class CompanyDetailsController {

    @Autowired
    private CompanyDetailsService companyDetailsService;

    @GetMapping
    public List<CompanyDetails> getAllDetails() {
        return companyDetailsService.getAllCompanyDetails();
    }

    @GetMapping("/active")
    public ResponseEntity<CompanyDetails> getActiveDetails() {
        return companyDetailsService.getActiveCompanyDetails()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDetails> getDetailsById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(companyDetailsService.getCompanyDetailsById(id));
    }

    @PostMapping
    public CompanyDetails createDetails(@RequestBody CompanyDetails companyDetails) {
        return companyDetailsService.createCompanyDetails(companyDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDetails> updateDetails(@PathVariable("id") Long id, @RequestBody CompanyDetails companyDetails) {
        return ResponseEntity.ok(companyDetailsService.updateCompanyDetails(id, companyDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetails(@PathVariable("id") Long id) {
        companyDetailsService.deleteCompanyDetails(id);
        return ResponseEntity.noContent().build();
    }
}

