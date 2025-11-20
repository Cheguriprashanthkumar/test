package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.model.ExternalClientData;
import org.nishgrid.clienterp.util.SystemInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Profile("external-api")
public class ProductionExternalClientDataService implements ExternalClientDataService {

    private final WebClient webClient;
    private final String clientDataPath;

    public ProductionExternalClientDataService(
            WebClient webClient,

            @Value("${external.license.api.path}") String clientDataPath) {

        this.webClient = webClient;
        this.clientDataPath = clientDataPath;
    }

    @Override
    public Optional<ExternalClientData> fetchClientData(String licenseKey) {


        LicenseValidateRequest request = new LicenseValidateRequest();
        request.setLicenseKey(licenseKey);
        request.setSystemId(SystemInfo.getSystemId());
        System.out.println("webclient " +licenseKey +" "+webClient+" "+clientDataPath);

        try {

            LicenseValidateResponse serverResponse = webClient.post()
                    .uri(this.clientDataPath)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(LicenseValidateResponse.class)
                    .block();

            if (serverResponse == null) {
                return Optional.empty();
            }


            ExternalClientData data = new ExternalClientData();
            data.setFullName(serverResponse.getFullName());
            data.setCompanyName(serverResponse.getCompanyName());
            data.setEmailAddress(serverResponse.getEmailAddress());


            return Optional.of(data);

        } catch (WebClientResponseException.NotFound ex) {
            return Optional.empty();
        } catch (Exception ex) {
            System.err.println("API error fetching license data for key " + licenseKey + ": " + ex.getMessage());
            return Optional.empty();
        }
    }


    private static class LicenseValidateRequest {
        private String licenseKey;
        private String systemId;


        public String getLicenseKey() { return licenseKey; }
        public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }
        public String getSystemId() { return systemId; }
        public void setSystemId(String systemId) { this.systemId = systemId; }
    }


    private static class LicenseValidateResponse {
        private String uniqueId;
        private String licenseKey;
        private String systemId;
        private String fullName;
        private String companyName;
        private String emailAddress;
        private LocalDate startDate;
        private LocalDate endDate;


        public String getUniqueId() { return uniqueId; }
        public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }
        public String getLicenseKey() { return licenseKey; }
        public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }
        public String getSystemId() { return systemId; }
        public void setSystemId(String systemId) { this.systemId = systemId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getEmailAddress() { return emailAddress; }
        public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }
}