package org.nishgrid.clienterp.service;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.model.ClientDetails;
import org.nishgrid.clienterp.repository.ClientDetailsRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class LoginService {

    private static final int MAX_ATTEMPTS = 5;

    private final ClientDetailsRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResult login(String emailAddress, String password) {
        Optional<ClientDetails> optionalClient = repository.findByEmailAddressIgnoreCase(emailAddress);

        if (optionalClient.isEmpty()) {
            return new LoginResult(false, null, "Client with this email not found.");
        }

        ClientDetails client = optionalClient.get();

        if (Boolean.TRUE.equals(client.getAdminAccountLocked())) {
            return new LoginResult(false, "ADMIN", "Your account is locked. Please contact support.");
        }

        if (client.getLicenseDetails() != null &&
                client.getLicenseDetails().getEndDate() != null &&
                client.getLicenseDetails().getEndDate().isBefore(LocalDate.now())) {
            return new LoginResult(false, "ADMIN", "License expired. Please renew your software.");
        }

        if (passwordEncoder.matches(password, client.getAdminPassword())) {
            client.setAdminFailedAttempts(0);
            client.setAdminAccountLocked(false);
            repository.save(client);
            return new LoginResult(true, "ADMIN", "Login successful. Welcome ADMIN.");
        } else {
            int attempts = client.getAdminFailedAttempts() + 1;
            client.setAdminFailedAttempts(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                client.setAdminAccountLocked(true);
                repository.save(client);
                return new LoginResult(false, "ADMIN", "Account locked due to too many failed attempts.");
            }

            repository.save(client);
            return new LoginResult(false, "ADMIN", "Invalid password. Attempts left: " + (MAX_ATTEMPTS - attempts));
        }
    }

    public String resetAdminPasswordByEmail(String orgToken, String emailAddress, String newPassword) {
        if (!"ORG_AUTHORIZED".equals(orgToken)) {
            return "Invalid organization token.";
        }

        Optional<ClientDetails> optionalClient = repository.findByEmailAddressIgnoreCase(emailAddress);
        if (optionalClient.isEmpty()) {
            return "Email not found.";
        }

        ClientDetails client = optionalClient.get();
        client.setAdminPassword(passwordEncoder.encode(newPassword));
        client.setAdminFailedAttempts(0);
        client.setAdminAccountLocked(false);
        repository.save(client);
        return "Password has been reset successfully for " + emailAddress;
    }
}
