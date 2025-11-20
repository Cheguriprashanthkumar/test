package org.nishgrid.clienterp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.nishgrid.clienterp.model.ClientDetails;
import org.nishgrid.clienterp.repository.ClientDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientDetailsService {

    private final ClientDetailsRepository repository;
    private final GeoNamesService geoNamesService;

    /**
     * Saves client details, ensuring the admin password is always securely hashed.
     */
    public ClientDetails save(ClientDetails details) {
        enrichWithGeoNamesIfNeeded(details);

        // Get the password from the request. This is plain text.
        String plainPassword = details.getAdminPassword();

        if (details.getId() != null) {
            // --- THIS IS AN UPDATE to an existing client ---

            // Find the existing details from the database
            ClientDetails existing = repository.findById(details.getId()).orElse(null);

            if (existing != null) {
                if (plainPassword == null || plainPassword.isBlank()) {
                    // Case 1: Update, but NO new password provided.
                    // Keep the existing, already-hashed password.
                    details.setAdminPassword(existing.getAdminPassword());
                } else {
                    // Case 2: Update WITH a new password.
                    // Hash the new plain text password.
                    String newHashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
                    details.setAdminPassword(newHashedPassword);
                }
            } else {
                // This case shouldn't happen, but if it does, hash if password exists
                if (plainPassword != null && !plainPassword.isBlank()) {
                    details.setAdminPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
                }
            }

        } else {
            // --- THIS IS A NEW CLIENT ---

            if (plainPassword != null && !plainPassword.isBlank()) {
                // Case 3: New client WITH a password.
                // Hash the plain text password.
                String newHashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
                details.setAdminPassword(newHashedPassword);
            }
            // Case 4: New client, NO password provided.
            // The password will just be saved as null.
        }

        // Save the details (with the now-hashed password) to the database
        return repository.save(details);
    }

    private void enrichWithGeoNamesIfNeeded(ClientDetails details) {
        if (details.getPincode() == null || details.getCountry() == null) return;
        try {
            Map<String, Object> postalData = geoNamesService.getPostalCodeDetails(
                    String.valueOf(details.getPincode()),
                    details.getCountry()
            );
            List<Map<String, Object>> codes = (List<Map<String, Object>>) postalData.get("postalcodes");
            if (codes == null || codes.isEmpty()) return;
            Map<String, Object> firstMatch = codes.get(0);
            if (details.getState() == null || details.getState().isBlank()) {
                details.setState((String) firstMatch.getOrDefault("adminName1", details.getState()));
            }
            if (details.getCity() == null || details.getCity().isBlank()) {
                details.setCity((String) firstMatch.getOrDefault("placeName", details.getCity()));
            }
        } catch (Exception ignored) {}
    }

    public List<ClientDetails> getAllClients() {
        return repository.findAll();
    }

    public Optional<ClientDetails> getClientById(Integer id) {
        return repository.findById(id);
    }
}