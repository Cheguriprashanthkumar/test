package org.nishgrid.clienterp.service;

import jakarta.transaction.Transactional;
import org.nishgrid.clienterp.model.ClientDetails;
import org.nishgrid.clienterp.model.ExternalClientData;
import org.nishgrid.clienterp.model.LicenseDetails;
import org.nishgrid.clienterp.model.LicenseResponse;
import org.nishgrid.clienterp.repository.ClientDetailsRepository;
import org.nishgrid.clienterp.repository.LicenseDetailsRepository;
import org.nishgrid.clienterp.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LicenseApiService {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private LicenseDetailsRepository licenseDetailsRepository;

    @Autowired
    private ClientDetailsRepository clientDetailsRepository;

    @Autowired
    private ExternalClientDataService externalClientDataService;

    // --- FIX 1: INJECT THE ClientDetailsService ---
    @Autowired
    private ClientDetailsService clientDetailsService;

    private void populateResponse(LicenseResponse response, ExternalClientData data) {
        response.setFullName(data.getFullName());
        response.setCompanyName(data.getCompanyName());
        response.setEmailAddress(data.getEmailAddress());
        response.setCompanyAddress(data.getCompanyAddress());
    }

    public LicenseResponse validateAndSaveLicense(Integer clientId, String licenseKey, String systemId) {
        if (licenseKey == null || licenseKey.isBlank()) {
            LicenseResponse r = new LicenseResponse();
            r.setValid(false);
            r.setMessage("License key cannot be empty.");
            return r;
        }

        LicenseResponse existing = licenseRepository.findByLicenseKey(licenseKey).orElse(null);
        if (existing != null) return existing;

        Optional<ExternalClientData> dataOpt = externalClientDataService.fetchClientData(licenseKey);
        if (dataOpt.isEmpty()) {
            LicenseResponse r = new LicenseResponse();
            r.setValid(false);
            r.setMessage("License key is invalid or client data could not be fetched.");
            return r;
        }

        ExternalClientData data = dataOpt.get();
        String uniqueId = UUID.randomUUID().toString();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusYears(1);

        LicenseResponse response = new LicenseResponse();
        response.setUniqueId(uniqueId);
        response.setLicenseKey(licenseKey);
        response.setSystemId(systemId);
        response.setStartDate(start.toString());
        response.setEndDate(end.toString());
        populateResponse(response, data);
        response.setValid(true);
        response.setSetupCompleted(true);
        response.setClientDetailsCompleted(false);
        response.setMessage("License validated successfully.");
        licenseRepository.save(response);

        LicenseDetails ld = new LicenseDetails();
        ld.setLicenseKey(licenseKey);
        ld.setStartDate(start);
        ld.setEndDate(end);

        if (clientId != null) {
            clientDetailsRepository.findById(clientId).ifPresent(ld::setClient);
        }

        licenseDetailsRepository.save(ld);
        return response;
    }

    public LicenseResponse getLicenseDetailsByKey(String licenseKey) {
        if (licenseKey == null || licenseKey.isBlank()) return null;

        LicenseResponse existing = licenseRepository.findByLicenseKey(licenseKey).orElse(null);
        if (existing != null) return existing;

        Optional<ExternalClientData> dataOpt = externalClientDataService.fetchClientData(licenseKey);
        if (dataOpt.isEmpty()) return null;

        ExternalClientData data = dataOpt.get();
        String uniqueId = UUID.randomUUID().toString();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusYears(1);

        LicenseResponse r = new LicenseResponse();
        r.setUniqueId(uniqueId);
        r.setLicenseKey(licenseKey);
        r.setSystemId("SYSTEM-001");
        r.setStartDate(start.toString());
        r.setEndDate(end.toString());
        populateResponse(r, data);
        r.setValid(true);
        r.setSetupCompleted(true);
        r.setClientDetailsCompleted(false);
        r.setMessage("Dummy license created automatically.");
        licenseRepository.save(r);

        LicenseDetails ld = new LicenseDetails();
        ld.setLicenseKey(licenseKey);
        ld.setStartDate(start);
        ld.setEndDate(end);
        licenseDetailsRepository.save(ld);

        return r;
    }

    public LicenseResponse getLicenseDetails(String uniqueId) {
        if (uniqueId == null || uniqueId.isBlank()) return null;
        return licenseRepository.findById(uniqueId).orElse(null);
    }

    public boolean isLicenseExpired(String uniqueId) {
        LicenseResponse r = getLicenseDetails(uniqueId);
        if (r == null || r.getEndDate() == null) return true;
        return LocalDate.now().isAfter(LocalDate.parse(r.getEndDate()));
    }

    public LicenseResponse validateAndUpdateLicense(String uniqueId, String licenseKey, String systemId) {
        if (uniqueId == null || uniqueId.isBlank()) return null;
        LicenseResponse r = licenseRepository.findById(uniqueId).orElse(null);
        if (r != null) {
            r.setSystemId(systemId);
            r.setSetupCompleted(true);
            r.setClientDetailsCompleted(true);
            r.setValid(true);
            licenseRepository.save(r);
        }
        return r;
    }

    @Transactional
    public String saveClientDetails(ClientDetails clientDetails) {
        // 1. Extract the license key from the incoming request
        String licenseKey = null;
        if (clientDetails.getLicenseDetails() != null) {
            licenseKey = clientDetails.getLicenseDetails().getLicenseKey();
        }

        // 2. IMPORTANT: Set the licenseDetails inside client to NULL before saving.
        // This prevents Hibernate from trying to INSERT a new (duplicate) license row via Cascade.
        clientDetails.setLicenseDetails(null);

        // --- FIX 2: CALL THE SERVICE, NOT THE REPOSITORY ---
        // The clientDetailsService.save() method contains all your BCrypt hashing logic.
        ClientDetails savedClient = clientDetailsService.save(clientDetails);
        // --- END OF FIX ---

        if (licenseKey == null || licenseKey.isBlank()) {
            return "Client saved, but license key not provided.";
        }

        // 4. Find the EXISTING license record
        Optional<LicenseDetails> licenseOpt = licenseDetailsRepository.findByLicenseKey(licenseKey);

        if (licenseOpt.isEmpty()) {
            // Handle edge case: Client saved, but the License Key provided doesn't exist in DB
            return "Client saved, but License Key '" + licenseKey + "' not found in database.";
        }

        // 5. Update the EXISTING license record with the new Client ID
        LicenseDetails existingLicense = licenseOpt.get();
        existingLicense.setClient(savedClient); // Link the client
        licenseDetailsRepository.save(existingLicense); // This performs an UPDATE, not an INSERT

        return "Client linked successfully with license key: " + licenseKey;
    }

    public LicenseResponse saveValidatedLicense(LicenseResponse r) {
        licenseRepository.save(r);

        List<LicenseDetails> list = licenseDetailsRepository.findAllByLicenseKey(r.getLicenseKey());
        LicenseDetails ld = list.isEmpty() ? new LicenseDetails() : list.get(0);

        ld.setLicenseKey(r.getLicenseKey());
        ld.setStartDate(LocalDate.parse(r.getStartDate()));
        ld.setEndDate(LocalDate.parse(r.getEndDate()));

        licenseDetailsRepository.save(ld);
        return r;
    }
}