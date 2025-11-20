package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorRepository vendorRepository;

    @PostMapping // Create
    public Vendor createVendor(@RequestBody Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @GetMapping // Read All
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @PutMapping("/{id}") // Update
    // FIX: Explicitly name the path variable "id"
    public ResponseEntity<Vendor> updateVendor(@PathVariable("id") Long id, @RequestBody Vendor vendorDetails) {
        return vendorRepository.findById(id)
                .map(vendor -> {
                    vendor.setName(vendorDetails.getName());
                    vendor.setEmail(vendorDetails.getEmail());
                    vendor.setPhone(vendorDetails.getPhone());
                    vendor.setGstNumber(vendorDetails.getGstNumber());
                    vendor.setContactPerson(vendorDetails.getContactPerson());
                    vendor.setAddress(vendorDetails.getAddress());
                    Vendor updatedVendor = vendorRepository.save(vendor);
                    return ResponseEntity.ok(updatedVendor);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // Delete
    // FIX: Explicitly name the path variable "id"
    public ResponseEntity<Void> deleteVendor(@PathVariable("id") Long id) {
        return vendorRepository.findById(id)
                .map(vendor -> {
                    vendorRepository.delete(vendor);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}