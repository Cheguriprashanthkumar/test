package org.nishgrid.clienterp.service;

import jakarta.transaction.Transactional;
import org.nishgrid.clienterp.dto.CustomerKycRequestDTO;
import org.nishgrid.clienterp.exception.ResourceNotFoundException;
import org.nishgrid.clienterp.model.Customer;
import org.nishgrid.clienterp.model.CustomerKyc;
import org.nishgrid.clienterp.repository.CustomerKycRepository;
import org.nishgrid.clienterp.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerKycService {

    private final CustomerKycRepository kycRepository;
    private final CustomerRepository customerRepository;

    public CustomerKycService(CustomerKycRepository kycRepository, CustomerRepository customerRepository) {
        this.kycRepository = kycRepository;
        this.customerRepository = customerRepository;
    }

    public List<CustomerKyc> findAllKyc() {
        return kycRepository.findAll();
    }

    @Transactional
    public CustomerKyc saveOrUpdateKyc(Long customerId, CustomerKycRequestDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        CustomerKyc kyc = kycRepository.findByCustomer_CustomerId(customerId)
                .orElse(new CustomerKyc());

        kyc.setCustomer(customer);
        kyc.setPanNumber(dto.getPanNumber());
        kyc.setAadhaarNumber(dto.getAadhaarNumber());
        kyc.setPhotoPath(dto.getPhotoPath());
        kyc.setRemarks(dto.getRemarks());
        kyc.setVerifiedOn(LocalDateTime.now());
        kyc.setVerifiedBy("SystemAdmin"); // In a real app, this would come from the security context

        return kycRepository.save(kyc);
    }

    @Transactional
    public void deleteKyc(Long customerId) {
        if (!kycRepository.findByCustomer_CustomerId(customerId).isPresent()) {
            throw new ResourceNotFoundException("KYC not found for customer id: " + customerId);
        }
        kycRepository.deleteByCustomer_CustomerId(customerId);
    }
}