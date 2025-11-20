package org.nishgrid.clienterp.service;

import jakarta.persistence.EntityNotFoundException;
import org.nishgrid.clienterp.model.BarcodeGeneration;
import org.nishgrid.clienterp.model.Employee;
import org.nishgrid.clienterp.repository.BarcodeGenerationRepository;
import org.nishgrid.clienterp.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class BarcodeGenerationService {

    @Autowired
    private BarcodeGenerationRepository barcodeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public BarcodeGeneration createBarcode(BarcodeGeneration barcodeRequest) {

        if (barcodeRequest.getGeneratedBy() == null || barcodeRequest.getGeneratedBy().getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID for 'generatedBy' must be provided.");
        }

        Integer employeeId = barcodeRequest.getGeneratedBy().getEmployeeId();
        Employee generator = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        barcodeRequest.setGeneratedBy(generator);

        String uniqueBarcodeValue = "ITM" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(100, 1000);
        barcodeRequest.setBarcodeValue(uniqueBarcodeValue);

        return barcodeRepository.save(barcodeRequest);
    }
}