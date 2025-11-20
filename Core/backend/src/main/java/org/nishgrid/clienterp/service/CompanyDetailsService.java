package org.nishgrid.clienterp.service;

import jakarta.persistence.EntityNotFoundException;
import org.nishgrid.clienterp.model.CompanyDetails;
import org.nishgrid.clienterp.repository.CompanyDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyDetailsService {

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Transactional(readOnly = true)
    public Optional<CompanyDetails> getActiveCompanyDetails() {
        return companyDetailsRepository.findByActive(true);
    }

    @Transactional(readOnly = true)
    public List<CompanyDetails> getAllCompanyDetails() {
        return companyDetailsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CompanyDetails getCompanyDetailsById(Long id) {
        return companyDetailsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CompanyDetails not found with id: " + id));
    }

    @Transactional
    public CompanyDetails createCompanyDetails(CompanyDetails companyDetails) {
        if (companyDetails.isActive()) {
            companyDetailsRepository.deactivateAll();
        }
        companyDetails.setId(null);
        return companyDetailsRepository.save(companyDetails);
    }

    @Transactional
    public CompanyDetails updateCompanyDetails(Long id, CompanyDetails detailsToUpdate) {
        CompanyDetails existingDetails = getCompanyDetailsById(id);

        if (detailsToUpdate.isActive()) {
            companyDetailsRepository.deactivateAll();
        }

        existingDetails.setCompanyName(detailsToUpdate.getCompanyName());
        existingDetails.setCompanyAddress(detailsToUpdate.getCompanyAddress());
        existingDetails.setCompanyContacts(detailsToUpdate.getCompanyContacts());
        existingDetails.setCompanyTagline(detailsToUpdate.getCompanyTagline());
        existingDetails.setActive(detailsToUpdate.isActive());

        return companyDetailsRepository.save(existingDetails);
    }

    @Transactional
    public void deleteCompanyDetails(Long id) {
        if (!companyDetailsRepository.existsById(id)) {
            throw new EntityNotFoundException("CompanyDetails not found with id: " + id);
        }
        companyDetailsRepository.deleteById(id);
    }
}