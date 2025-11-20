package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.JewelryExchangeRequestDTO;
import org.nishgrid.clienterp.dto.ExchangeLogRecordDTO;
import org.nishgrid.clienterp.model.SalesExchange;
import org.nishgrid.clienterp.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchanges")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @PostMapping("/jewelry")
    public ResponseEntity<SalesExchange> processJewelryExchange(@RequestBody JewelryExchangeRequestDTO exchangeRequestDTO) {
        try {
            SalesExchange createdExchange = exchangeService.processJewelryExchange(exchangeRequestDTO);
            return new ResponseEntity<>(createdExchange, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/log")
    public ResponseEntity<List<ExchangeLogRecordDTO>> getExchangeLog() {
        List<ExchangeLogRecordDTO> exchangeLog = exchangeService.getExchangeLog();
        return ResponseEntity.ok(exchangeLog);
    }
}