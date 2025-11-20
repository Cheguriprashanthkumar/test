package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDetailsDto {
    private Integer id;
    private String fullName;
    private String companyName;
    private String emailAddress;
    private Long mobileNumber;
    private String logo;
    private String gstNumber;
    private String address;
    private String state;
    private Integer pincode;
    private String country;
    private String city;

    // ✅ Nested object for license details
    private LicenseDetailsDto licenseDetails;

    // ✅ Override unnecessary static method
    // Removed invalid getid() recursion

    @Override
    public String toString() {
        return companyName + " (" + fullName + ")";
    }

    // ✅ Inner DTO class for license info
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LicenseDetailsDto {
        private Long id;
        private String licenseKey;
        private String startDate;
        private String endDate;
    }
}
