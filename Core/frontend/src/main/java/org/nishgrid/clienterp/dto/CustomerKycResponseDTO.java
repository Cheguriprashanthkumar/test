package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.CustomerKyc;
import java.time.LocalDateTime;

public class CustomerKycResponseDTO {
    private Long kycId;
    private Long customerId;
    private String customerName;
    private String panNumber;
    private String aadhaarNumber;
    private String photoPath;
    private String verifiedBy;
    private LocalDateTime verifiedOn;
    private String remarks;

    public static CustomerKycResponseDTO fromEntity(CustomerKyc kyc) {
        CustomerKycResponseDTO dto = new CustomerKycResponseDTO();
        dto.setKycId(kyc.getKycId());
        dto.setPanNumber(kyc.getPanNumber());
        dto.setAadhaarNumber(kyc.getAadhaarNumber());
        dto.setPhotoPath(kyc.getPhotoPath());
        dto.setVerifiedBy(kyc.getVerifiedBy());
        dto.setVerifiedOn(kyc.getVerifiedOn());
        dto.setRemarks(kyc.getRemarks());
        if (kyc.getCustomer() != null) {
            dto.setCustomerId(kyc.getCustomer().getCustomerId());
            dto.setCustomerName(kyc.getCustomer().getName());
        }
        return dto;
    }

    public Long getKycId() { return kycId; }
    public void setKycId(Long kycId) { this.kycId = kycId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
    public LocalDateTime getVerifiedOn() { return verifiedOn; }
    public void setVerifiedOn(LocalDateTime verifiedOn) { this.verifiedOn = verifiedOn; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
