package org.nishgrid.clienterp.dto;

import java.time.LocalDateTime;

public class CustomerKycDTO {
    private Long kycId;
    private Long customerId;
    private String panNumber;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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