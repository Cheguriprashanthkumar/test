package org.nishgrid.clienterp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "license_data")
public class LicenseResponse {

    @Id
    private String uniqueId;

    @Column(name = "license_key")
    private String licenseKey;

    @Column(name = "system_id")
    private String systemId;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "setup_completed")
    private boolean setupCompleted;

    @Column(name = "client_details_completed")
    private boolean clientDetailsCompleted;

    @Column(name = "valid")
    private boolean valid;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "email_address")
    private String emailAddress;
    private String redirectUrl;
    private String companyAddress;

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Transient
    private String message;

    // Getters and Setters
    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public String getLicenseKey() { return licenseKey; }
    public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }

    public String getSystemId() { return systemId; }
    public void setSystemId(String systemId) { this.systemId = systemId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isSetupCompleted() { return setupCompleted; }
    public void setSetupCompleted(boolean setupCompleted) { this.setupCompleted = setupCompleted; }

    public boolean isClientDetailsCompleted() { return clientDetailsCompleted; }
    public void setClientDetailsCompleted(boolean clientDetailsCompleted) { this.clientDetailsCompleted = clientDetailsCompleted; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
