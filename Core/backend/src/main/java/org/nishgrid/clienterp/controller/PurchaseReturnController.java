package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.PurchaseReturnRequest;
import org.nishgrid.clienterp.dto.PurchaseReturnResponse;
import org.nishgrid.clienterp.service.PurchaseReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-returns")
public class PurchaseReturnController {

    @Autowired
    private PurchaseReturnService returnService;

    @PostMapping
    public ResponseEntity<PurchaseReturnResponse> createReturn(@RequestBody PurchaseReturnRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(returnService.createReturn(request));
    }

    @GetMapping
    public List<PurchaseReturnResponse> getAllReturns() {
        return returnService.getAllReturns();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseReturnResponse> getReturnById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(returnService.getReturnById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseReturnResponse> updateReturn(@PathVariable("id") Long id, @RequestBody PurchaseReturnRequest request) {
        return ResponseEntity.ok(returnService.updateReturn(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturn(@PathVariable("id") Long id) {
        returnService.deleteReturn(id);
        return ResponseEntity.noContent().build();
    }
}