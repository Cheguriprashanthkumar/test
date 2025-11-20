package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "client_details_form")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;
    private String companyName;

    @Column(unique = true)
    private String emailAddress;

    private String companyType;

    @Column(name = "admin_password")
    private String adminPassword;

    @NotNull(message = "Mobile number is required")
    @Digits(integer = 15, fraction = 0, message = "Invalid mobile number")
    private Long mobileNumber;

    @NotBlank(message = "Logo file path is required")
    private String logo;

    @NotBlank(message = "GST Number is required")
    private String gstNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "State is required")
    private String state;

    @NotNull(message = "Pincode is required")
    private Integer pincode;

    @NotBlank(message = "Country is required")
    private String country;

    private String city;
    private LocalDate lastLogin;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private LicenseDetails licenseDetails;

    @Column(name = "admin_failed_attempts")
    private Integer adminFailedAttempts = 0;

    @Column(name = "admin_account_locked")
    private Boolean adminAccountLocked = false;

    public void addLicense(LicenseDetails licenseDetails) {
        this.licenseDetails = licenseDetails;
        licenseDetails.setClient(this);
    }
}
