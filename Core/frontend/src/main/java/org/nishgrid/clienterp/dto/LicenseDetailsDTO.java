package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Ignores any unknown properties in the JSON to prevent parsing errors
@JsonIgnoreProperties(ignoreUnknown = true)
public class LicenseDetailsDTO {

    private String companyName;
    private String fullName;
    private String endDate; // Received as a String "yyyy-MM-dd"

    // Getters and Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}