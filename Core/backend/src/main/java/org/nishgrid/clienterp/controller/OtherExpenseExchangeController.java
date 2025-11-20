package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.OtherExpenseExchangeRequest;
import org.nishgrid.clienterp.dto.OtherExpenseExchangeResponse;
import org.nishgrid.clienterp.service.OtherExpenseExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/other-expense-exchanges")
public class OtherExpenseExchangeController {

    @Autowired
    private OtherExpenseExchangeService exchangeService;

    @PostMapping
    public ResponseEntity<OtherExpenseExchangeResponse> createExchange(@RequestBody OtherExpenseExchangeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exchangeService.createExchange(request));
    }

    @GetMapping
    public List<OtherExpenseExchangeResponse> getAllExchanges() {
        return exchangeService.getAllExchanges();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExchange(@PathVariable("id") Long id) {
        exchangeService.deleteExchange(id);
        return ResponseEntity.noContent().build();
    }
}