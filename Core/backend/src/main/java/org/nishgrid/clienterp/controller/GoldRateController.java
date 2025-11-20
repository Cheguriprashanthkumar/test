package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.GoldRate;
import org.nishgrid.clienterp.service.GoldRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Import this class
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gold-rates")
public class GoldRateController {

    @Autowired
    private GoldRateService goldRateService;

    @GetMapping("/latest")
    public ResponseEntity<GoldRate> getLatestRates() { // Change return type to ResponseEntity

        GoldRate latestRate = goldRateService.getLatestRate();

        // Check if a record was found in the database
        if (latestRate != null) {
            // If found, return 200 OK with the data in the body
            return ResponseEntity.ok(latestRate);
        } else {

            return ResponseEntity.notFound().build();
        }
    }
}