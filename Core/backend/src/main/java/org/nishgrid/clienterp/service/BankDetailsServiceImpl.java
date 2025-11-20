package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.BankDetailsRequest;
import org.nishgrid.clienterp.dto.BankDetailsResponse;
import org.nishgrid.clienterp.model.BankDetails;
import org.nishgrid.clienterp.repository.BankDetailsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BankDetailsServiceImpl implements BankDetailsService {

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Override
    public BankDetailsResponse createBankDetails(BankDetailsRequest request, MultipartFile file) {
        BankDetails bankDetails = new BankDetails();
        BeanUtils.copyProperties(request, bankDetails);

        if (file != null && !file.isEmpty()) {
            try {
                bankDetails.setQrCodeData(file.getBytes());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to process QR code image file", e);
            }
        }

        BankDetails savedDetails = bankDetailsRepository.save(bankDetails);
        return BankDetailsResponse.fromEntity(savedDetails);
    }

    @Override
    public BankDetailsResponse updateBankDetails(Long id, BankDetailsRequest request, MultipartFile file) {
        BankDetails bankDetails = bankDetailsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank details not found"));
        BeanUtils.copyProperties(request, bankDetails);

        if (file != null && !file.isEmpty()) {
            try {
                bankDetails.setQrCodeData(file.getBytes());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to process QR code image file", e);
            }
        }

        BankDetails updatedDetails = bankDetailsRepository.save(bankDetails);
        return BankDetailsResponse.fromEntity(updatedDetails);
    }

    @Override
    public List<BankDetailsResponse> getAllBankDetails() {
        return bankDetailsRepository.findAll().stream()
                .map(BankDetailsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public BankDetailsResponse getBankDetailsById(Long id) {
        return bankDetailsRepository.findById(id)
                .map(BankDetailsResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank details not found"));
    }

    @Override
    public void deleteBankDetails(Long id) {
        if (!bankDetailsRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank details not found");
        }
        bankDetailsRepository.deleteById(id);
    }
}