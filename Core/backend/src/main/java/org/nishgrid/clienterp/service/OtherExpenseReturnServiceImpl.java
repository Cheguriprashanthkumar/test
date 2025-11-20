package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.OtherExpenseReturnRequest;
import org.nishgrid.clienterp.dto.OtherExpenseReturnResponse;
import org.nishgrid.clienterp.model.OtherExpense;
import org.nishgrid.clienterp.model.OtherExpenseReturn;
import org.nishgrid.clienterp.repository.OtherExpenseRepository;
import org.nishgrid.clienterp.repository.OtherExpenseReturnRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OtherExpenseReturnServiceImpl implements OtherExpenseReturnService {

    @Autowired private OtherExpenseReturnRepository returnRepository;
    @Autowired private OtherExpenseRepository expenseRepository;
    @Autowired private FileStorageService fileStorageService;

    @Override
    public OtherExpenseReturnResponse createReturn(OtherExpenseReturnRequest request, MultipartFile file) {
        OtherExpense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Original expense not found"));

        OtherExpenseReturn expenseReturn = new OtherExpenseReturn();
        mapDtoToEntity(request, expense, expenseReturn, file);

        OtherExpenseReturn savedReturn = returnRepository.save(expenseReturn);
        return OtherExpenseReturnResponse.fromEntity(savedReturn);
    }

    @Override
    public List<OtherExpenseReturnResponse> getAllReturns() {
        return returnRepository.findAllWithDetails().stream()
                .map(OtherExpenseReturnResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public OtherExpenseReturnResponse getReturnById(Long id) {
        return returnRepository.findById(id)
                .map(OtherExpenseReturnResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return not found"));
    }

    @Override
    public OtherExpenseReturnResponse updateReturn(Long id, OtherExpenseReturnRequest request, MultipartFile file) {
        OtherExpenseReturn expenseReturn = returnRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return not found"));

        OtherExpense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Original expense not found"));

        mapDtoToEntity(request, expense, expenseReturn, file);

        OtherExpenseReturn updatedReturn = returnRepository.save(expenseReturn);
        return OtherExpenseReturnResponse.fromEntity(updatedReturn);
    }

    @Override
    public void deleteReturn(Long id) {
        OtherExpenseReturn expenseReturn = returnRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return not found"));

        if (expenseReturn.getAttachmentPath() != null) {
            fileStorageService.deleteFile(expenseReturn.getAttachmentPath());
        }
        returnRepository.deleteById(id);
    }

    private void mapDtoToEntity(OtherExpenseReturnRequest request, OtherExpense expense, OtherExpenseReturn expenseReturn, MultipartFile file) {
        BeanUtils.copyProperties(request, expenseReturn);
        expenseReturn.setOtherExpense(expense);
        if (file != null && !file.isEmpty()) {
            if (expenseReturn.getAttachmentPath() != null) {
                fileStorageService.deleteFile(expenseReturn.getAttachmentPath());
            }
            String fileName = fileStorageService.storeFile(file);
            expenseReturn.setAttachmentPath(fileName);
        }
    }
}