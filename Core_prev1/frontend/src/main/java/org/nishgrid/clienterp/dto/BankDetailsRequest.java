package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.BankDetails;

public class BankDetailsRequest {
    private String accountName;
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscCode;
    private BankDetails.Status status;


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
    public BankDetails.Status getStatus() { return status; }
    public void setStatus(BankDetails.Status status) { this.status = status; }
}