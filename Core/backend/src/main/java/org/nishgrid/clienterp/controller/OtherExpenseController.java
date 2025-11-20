package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.CancelExpenseRequest;
import org.nishgrid.clienterp.dto.OtherExpenseRequest;
import org.nishgrid.clienterp.dto.OtherExpenseResponse;
import org.nishgrid.clienterp.service.OtherExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/other-expenses")
public class OtherExpenseController {

    @Autowired
    private OtherExpenseService expenseService;

    @PostMapping
    public ResponseEntity<OtherExpenseResponse> createExpense(@RequestPart("expense") OtherExpenseRequest request,
                                                              @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.createExpense(request, file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OtherExpenseResponse> updateExpense(@PathVariable("id") Long id,
                                                              @RequestPart("expense") OtherExpenseRequest request,
                                                              @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request, file));
    }

    @GetMapping
    public List<OtherExpenseResponse> getAllExpenses() { return expenseService.getAllExpenses(); }

    @GetMapping("/{id}")
    public ResponseEntity<OtherExpenseResponse> getExpenseById(@PathVariable("id") Long id) { return ResponseEntity.ok(expenseService.getExpenseById(id)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable("id") Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelExpense(@PathVariable("id") Long id, @RequestBody CancelExpenseRequest request) {
        expenseService.cancelExpense(id, request);
        return ResponseEntity.ok().build();
    }
}