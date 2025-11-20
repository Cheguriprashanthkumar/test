package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.BankDetails;
import java.util.Objects;

public class BankDetailsResponse {
    private Long bankId;
    private String accountName;
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscCode;
    private String qrCodePath;
    private String qrCodeData;
    private BankDetails.Status status;

    public Long getBankId() { return bankId; }
    public void setBankId(Long bankId) { this.bankId = bankId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getQrCodePath() { return qrCodePath; }
    public void setQrCodePath(String qrCodePath) { this.qrCodePath = qrCodePath; }
    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }
    public BankDetails.Status getStatus() { return status; }
    public void setStatus(BankDetails.Status status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankDetailsResponse that = (BankDetailsResponse) o;
        return Objects.equals(bankId, that.bankId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankId);
    }
}