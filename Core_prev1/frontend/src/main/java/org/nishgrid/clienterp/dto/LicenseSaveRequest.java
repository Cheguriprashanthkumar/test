package org.nishgrid.clienterp.dto;

public class LicenseSaveRequest {

    private String uniqueId;
    private String licenseKey;
    private String systemId;
    private String startDate;
    private String endDate;
    private boolean setupCompleted;
    private boolean clientDetailsCompleted;
    private boolean valid;
    private String fullName;
    private String companyName;
    private String emailAddress;
    private String companyAddress;

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isSetupCompleted() {
        return setupCompleted;
    }

    public void setSetupCompleted(boolean setupCompleted) {
        this.setupCompleted = setupCompleted;
    }

    public boolean isClientDetailsCompleted() {
        return clientDetailsCompleted;
    }

    public void setClientDetailsCompleted(boolean clientDetailsCompleted) {
        this.clientDetailsCompleted = clientDetailsCompleted;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
}
