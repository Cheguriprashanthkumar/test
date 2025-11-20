package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.GrnRequest;
import org.nishgrid.clienterp.dto.GrnResponse;
import org.nishgrid.clienterp.model.GoodsReceiptNote;
import org.nishgrid.clienterp.service.GrnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grn")
public class GrnController {

    @Autowired
    private GrnService grnService;

    @PostMapping
    public ResponseEntity<GrnResponse> createGrn(@RequestBody GrnRequest request) {
        GrnResponse createdGrn = grnService.createGrn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGrn);
    }

    @GetMapping
    public List<GoodsReceiptNote> getAllGrns() {
        return grnService.getAllGrns();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrnResponse> updateGrn(@PathVariable("id") Long id, @RequestBody GrnRequest request) {
        GrnResponse updatedGrn = grnService.updateGrn(id, request);
        return ResponseEntity.ok(updatedGrn);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrn(@PathVariable("id") Long id) {
        grnService.deleteGrn(id);
        return ResponseEntity.noContent().build();
    }
}