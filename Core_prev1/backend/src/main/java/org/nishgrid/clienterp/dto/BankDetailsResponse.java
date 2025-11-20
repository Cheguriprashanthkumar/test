package org.nishgrid.clienterp.dto;

import org.nishgrid.clienterp.model.BankDetails;

public record BankDetailsResponse(
        Long bankId,
        String accountName,
        String bankName,
        String branchName,
        String accountNumber,
        String ifscCode,
        byte[] qrCodeData, // Changed to byte[]
        BankDetails.Status status
) {
    public static BankDetailsResponse fromEntity(BankDetails bankDetails) {
        return new BankDetailsResponse(
                bankDetails.getBankId(),
                bankDetails.getAccountName(),
                bankDetails.getBankName(),
                bankDetails.getBranchName(),
                bankDetails.getAccountNumber(),
                bankDetails.getIfscCode(),
                bankDetails.getQrCodeData(), // Changed to get qrCodeData
                bankDetails.getStatus()
        );
    }
}