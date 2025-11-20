package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.BankDetails;

@Data
public class BankDetailsRequest {
    private String accountName;
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String ifscCode;
    private BankDetails.Status status;
}