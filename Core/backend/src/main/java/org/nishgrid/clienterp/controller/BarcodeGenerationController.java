package org.nishgrid.clienterp.controller;

import jakarta.persistence.EntityNotFoundException;
import org.nishgrid.clienterp.model.BarcodeGeneration;
import org.nishgrid.clienterp.service.BarcodeGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/barcodes")
public class BarcodeGenerationController {

    private static final Logger logger = LoggerFactory.getLogger(BarcodeGenerationController.class);

    @Autowired
    private BarcodeGenerationService barcodeService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateBarcode(@RequestBody BarcodeGeneration barcodeRequest) {
        try {
            BarcodeGeneration newBarcode = barcodeService.createBarcode(barcodeRequest);
            return new ResponseEntity<>(newBarcode, HttpStatus.CREATED);

        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warn("Bad Request for barcode generation: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("An unexpected error occurred during barcode generation", e);
            return new ResponseEntity<>("An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}