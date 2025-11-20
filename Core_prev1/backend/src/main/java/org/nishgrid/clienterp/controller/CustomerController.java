package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.CustomerDTO;
import org.nishgrid.clienterp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping

    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerRepository.findAll().stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }
    @GetMapping("/selection")
    public ResponseEntity<List<CustomerDTO>> getAllCustomersForSelection() {
        List<CustomerDTO> customers = customerRepository.findAll().stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }
}