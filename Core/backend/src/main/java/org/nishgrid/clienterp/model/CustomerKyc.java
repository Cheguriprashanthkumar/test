package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_kyc")
public class CustomerKyc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kycId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN card number format")
    @Column(nullable = false)
    private String panNumber;

    @Column(nullable = false)
    private String aadhaarNumber;

    private String photoPath;
    private String verifiedBy;
    private LocalDateTime verifiedOn;
    private String remarks;

    public Long getKycId() {
        return kycId;
    }

    public void setKycId(Long kycId) {
        this.kycId = kycId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getVerifiedOn() {
        return verifiedOn;
    }

    public void setVerifiedOn(LocalDateTime verifiedOn) {
        this.verifiedOn = verifiedOn;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}