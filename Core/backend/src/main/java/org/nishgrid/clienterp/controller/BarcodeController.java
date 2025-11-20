package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.BarcodeRequest;
import org.nishgrid.clienterp.dto.BarcodeResponse;
import org.nishgrid.clienterp.service.BarcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barcodes")
public class BarcodeController {

    @Autowired
    private BarcodeService barcodeService;

    @PostMapping
    public ResponseEntity<List<BarcodeResponse>> createBarcodes(@RequestBody BarcodeRequest request) {
        List<BarcodeResponse> createdBarcodes = barcodeService.createBarcodes(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBarcodes);
    }

    @GetMapping("/grn/{grnId}")
    public List<BarcodeResponse> getBarcodesByGrn(@PathVariable("grnId") Long grnId) {
        return barcodeService.getBarcodesByGrnId(grnId);
    }
}