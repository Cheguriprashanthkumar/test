package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.ClientDetails;
import org.nishgrid.clienterp.model.LicenseRequest;
import org.nishgrid.clienterp.model.LicenseResponse;
import org.nishgrid.clienterp.service.LicenseApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/license")
@CrossOrigin(origins = "*")
public class LicenseController {

    @Autowired
    private LicenseApiService licenseApiService;

    @PostMapping("/validate")
    public ResponseEntity<LicenseResponse> validate(@RequestBody LicenseRequest request) {
        LicenseResponse response = licenseApiService.validateAndSaveLicense(
                request.getClientId(),
                request.getLicenseKey(),
                request.getSystemId()
        );
        if (response.isValid()) {
            response.setMessage("License valid. Redirect to client form.");
            response.setRedirectUrl("/client-form");
        }
        return ResponseEntity.ok(response);
    }
    @PostMapping("/save-valid")
    public ResponseEntity<LicenseResponse> saveValidLicense(@RequestBody LicenseResponse request) {
        LicenseResponse saved = licenseApiService.saveValidatedLicense(request);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/update")
    public ResponseEntity<LicenseResponse> updateLicense(@RequestBody LicenseResponse updatedLicense) {
        LicenseResponse response = licenseApiService.validateAndUpdateLicense(
                updatedLicense.getUniqueId(),
                updatedLicense.getLicenseKey(),
                updatedLicense.getSystemId()
        );
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/prefill")
    public ResponseEntity<LicenseResponse> prefillLicenseData(@RequestParam(name = "licenseKey") String licenseKey) {
        LicenseResponse response = licenseApiService.getLicenseDetailsByKey(licenseKey);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/details")
    public ResponseEntity<LicenseResponse> getLicense(@RequestParam(name = "uniqueId") String uniqueId) {
        LicenseResponse response = licenseApiService.getLicenseDetails(uniqueId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PostMapping("/client/save")
    public ResponseEntity<String> saveClientForm(@RequestBody @Valid ClientDetails clientDetails) {
        String result = licenseApiService.saveClientDetails(clientDetails);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/details/{uniqueId}")
    public ResponseEntity<LicenseResponse> getLicenseDetails(@PathVariable("uniqueId") String uniqueId) {
        LicenseResponse licenseDetails = licenseApiService.getLicenseDetails(uniqueId);
        return licenseDetails != null ? ResponseEntity.ok(licenseDetails) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-key/{licenseKey}")
    public ResponseEntity<LicenseResponse> getByLicenseKey(@PathVariable("licenseKey") String licenseKey) {
        LicenseResponse response = licenseApiService.getLicenseDetailsByKey(licenseKey);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-id/{uniqueId}")
    public ResponseEntity<LicenseResponse> getByUniqueId(@PathVariable("uniqueId") String uniqueId) {
        LicenseResponse response = licenseApiService.getLicenseDetails(uniqueId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/expired/{uniqueId}")
    public ResponseEntity<Boolean> isExpired(@PathVariable("uniqueId") String uniqueId) {
        boolean expired = licenseApiService.isLicenseExpired(uniqueId);
        return ResponseEntity.ok(expired);
    }
}
