package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.BankDetailsRequest;
import org.nishgrid.clienterp.dto.BankDetailsResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface BankDetailsService {
    BankDetailsResponse createBankDetails(BankDetailsRequest request, MultipartFile file);
    List<BankDetailsResponse> getAllBankDetails();
    BankDetailsResponse getBankDetailsById(Long id);
    BankDetailsResponse updateBankDetails(Long id, BankDetailsRequest request, MultipartFile file);
    void deleteBankDetails(Long id);
}