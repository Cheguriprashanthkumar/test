package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.HsnSummary;
import org.nishgrid.clienterp.service.HsnSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hsn-summary")
public class HsnSummaryController {

    @Autowired
    private HsnSummaryService hsnSummaryService;

    @GetMapping
    public List<HsnSummary> getHsnSummary(@RequestParam(value = "month", required = false) String month) {
        return hsnSummaryService.getSummaries(month);
    }

    /**
     * Now accepts year and month parameters for flexible testing.
     * If no parameters are given, it defaults to the previous month.
     * e.g., POST /api/hsn-summary/generate?year=2025&month=9
     */
    @PostMapping("/generate")
    public ResponseEntity<String> triggerSummaryGeneration(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {
        try {
            if (year != null && month != null) {
                hsnSummaryService.generateMonthlyHsnSummary(year, month);
                return ResponseEntity.ok("Successfully generated HSN summary for " + year + "-" + String.format("%02d", month) + ".");
            } else {
                hsnSummaryService.generateMonthlyHsnSummary();
                return ResponseEntity.ok("Successfully generated HSN summary for the previous month.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during summary generation: " + e.getMessage());
        }
    }
}

