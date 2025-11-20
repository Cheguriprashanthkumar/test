package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.OtherExpenseReturnRequest;
import org.nishgrid.clienterp.dto.OtherExpenseReturnResponse;
import org.nishgrid.clienterp.service.OtherExpenseReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/other-expense-returns")
public class OtherExpenseReturnController {

    @Autowired
    private OtherExpenseReturnService returnService;

    @PostMapping
    public ResponseEntity<OtherExpenseReturnResponse> createReturn(@RequestPart("return") OtherExpenseReturnRequest request,
                                                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(returnService.createReturn(request, file));
    }

    @GetMapping
    public List<OtherExpenseReturnResponse> getAllReturns() {
        return returnService.getAllReturns();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OtherExpenseReturnResponse> getReturnById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(returnService.getReturnById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OtherExpenseReturnResponse> updateReturn(@PathVariable("id") Long id,
                                                                   @RequestPart("return") OtherExpenseReturnRequest request,
                                                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(returnService.updateReturn(id, request, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturn(@PathVariable("id") Long id) {
        returnService.deleteReturn(id);
        return ResponseEntity.noContent().build();
    }
}