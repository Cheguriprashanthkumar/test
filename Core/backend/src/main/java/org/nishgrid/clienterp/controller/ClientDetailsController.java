package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.model.ClientDetails;
import org.nishgrid.clienterp.model.LicenseResponse;
import org.nishgrid.clienterp.repository.LicenseDetailsRepository;
import org.nishgrid.clienterp.service.ClientDetailsService;
import org.nishgrid.clienterp.service.LicenseApiService;
import org.nishgrid.clienterp.util.SystemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin
public class ClientDetailsController {

    @Autowired
    private final LicenseDetailsRepository licenseDetailsRepository;

    @Autowired
    private final LicenseApiService licenseApiService;

    private final ClientDetailsService service;



    @PostMapping("/validate-license")
    public ResponseEntity<LicenseResponse> validateLicense(
            @RequestParam Integer clientId,
            @RequestParam String licenseKey) {

        String systemId = SystemInfo.getSystemId();

        LicenseResponse response = licenseApiService.validateAndSaveLicense(
                clientId,
                licenseKey,
                systemId
        );

        return ResponseEntity.ok(response);
    }

    // ✅ Save a new client
    @PostMapping
    public ClientDetails saveClient(@RequestBody ClientDetails details) {
        return service.save(details);
    }

    // ✅ Get all clients
    @GetMapping
    public List<ClientDetails> getAllClients() {
        return service.getAllClients();
    }

    // ✅ Get a single client by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientDetails> getClientById(@PathVariable("id") Integer id) {
        Optional<ClientDetails> client = service.getClientById(id);
        return client.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/by-license/{licenseKey}")
    public ResponseEntity<ClientDetails> getClientByLicenseKey(@PathVariable("licenseKey") String licenseKey) {
        return licenseDetailsRepository.findByLicenseKey(licenseKey)
                .map(license -> ResponseEntity.ok(license.getClient()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
