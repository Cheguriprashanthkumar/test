package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.OtherExpenseExchangeRequest;
import org.nishgrid.clienterp.dto.OtherExpenseExchangeResponse;
import org.nishgrid.clienterp.model.OtherExpense;
import org.nishgrid.clienterp.model.OtherExpenseExchange;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.repository.OtherExpenseExchangeRepository;
import org.nishgrid.clienterp.repository.OtherExpenseRepository;
import org.nishgrid.clienterp.repository.VendorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OtherExpenseExchangeServiceImpl implements OtherExpenseExchangeService {

    @Autowired private OtherExpenseExchangeRepository exchangeRepository;
    @Autowired private OtherExpenseRepository expenseRepository;
    @Autowired private VendorRepository vendorRepository;

    @Override
    public OtherExpenseExchangeResponse createExchange(OtherExpenseExchangeRequest request) {
        OtherExpense oldExpense = expenseRepository.findById(request.getOldExpenseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Old expense not found"));
        OtherExpense newExpense = expenseRepository.findById(request.getNewExpenseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "New expense not found"));

        Vendor vendor = null;
        if (request.getVendorId() != null) {
            vendor = vendorRepository.findById(request.getVendorId()).orElse(null);
        }

        OtherExpenseExchange exchange = new OtherExpenseExchange();
        BeanUtils.copyProperties(request, exchange);
        exchange.setOldExpense(oldExpense);
        exchange.setNewExpense(newExpense);
        exchange.setVendor(vendor);

        OtherExpenseExchange savedExchange = exchangeRepository.save(exchange);
        return OtherExpenseExchangeResponse.fromEntity(savedExchange);
    }

    @Override
    public List<OtherExpenseExchangeResponse> getAllExchanges() {
        return exchangeRepository.findAllWithDetails().stream()
                .map(OtherExpenseExchangeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteExchange(Long id) {
        if (!exchangeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exchange record not found");
        }
        exchangeRepository.deleteById(id);
    }
}