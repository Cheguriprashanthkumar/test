package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.DiscountDTO;
import org.nishgrid.clienterp.model.Discount;
import org.nishgrid.clienterp.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @GetMapping
    public List<Discount> getAllDiscounts() {
        return discountService.getAllDiscounts();
    }

    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody DiscountDTO dto) {
        return new ResponseEntity<>(discountService.createDiscount(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Long id, @RequestBody DiscountDTO dto) {
        return ResponseEntity.ok(discountService.updateDiscount(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}