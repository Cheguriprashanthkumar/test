package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.CancelExpenseRequest;
import org.nishgrid.clienterp.dto.OtherExpenseRequest;
import org.nishgrid.clienterp.dto.OtherExpenseResponse;
import org.nishgrid.clienterp.dto.ReportRequest;
import org.nishgrid.clienterp.model.CancelledExpenseLog;
import org.nishgrid.clienterp.model.OtherExpense;
import org.nishgrid.clienterp.model.OtherExpenseReturn;
import org.nishgrid.clienterp.repository.CancelledExpenseLogRepository;
import org.nishgrid.clienterp.repository.OtherExpenseRepository;
import org.nishgrid.clienterp.repository.OtherExpenseReturnRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OtherExpenseServiceImpl implements OtherExpenseService {

    @Autowired
    private OtherExpenseRepository expenseRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CancelledExpenseLogRepository cancelledExpenseLogRepository;

    @Autowired
    private OtherExpenseReturnRepository expenseReturnRepository; // This was the missing dependency

    @Override
    public OtherExpenseResponse createExpense(OtherExpenseRequest request, MultipartFile file) {
        OtherExpense expense = new OtherExpense();
        BeanUtils.copyProperties(request, expense);

        if (file != null && !file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            expense.setBillAttachment(fileName);
        }

        OtherExpense savedExpense = expenseRepository.save(expense);
        return OtherExpenseResponse.fromEntity(savedExpense);
    }

    @Override
    public OtherExpenseResponse updateExpense(Long id, OtherExpenseRequest request, MultipartFile file) {
        OtherExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        BeanUtils.copyProperties(request, expense);

        if (file != null && !file.isEmpty()) {
            if (expense.getBillAttachment() != null) {
                fileStorageService.deleteFile(expense.getBillAttachment());
            }
            String fileName = fileStorageService.storeFile(file);
            expense.setBillAttachment(fileName);
        }

        OtherExpense updatedExpense = expenseRepository.save(expense);
        return OtherExpenseResponse.fromEntity(updatedExpense);
    }

    @Override
    public List<OtherExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll(Sort.by(Sort.Direction.DESC, "expenseDate")).stream()
                .map(OtherExpenseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public OtherExpenseResponse getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .map(OtherExpenseResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
    }

    @Override
    public void deleteExpense(Long id) {
        OtherExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        if (expense.getBillAttachment() != null && !expense.getBillAttachment().isBlank()) {
            fileStorageService.deleteFile(expense.getBillAttachment());
        }

        expenseRepository.deleteById(id);
    }

    @Override
    public void cancelExpense(Long id, CancelExpenseRequest request) {
        OtherExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        expense.setApprovalStatus(OtherExpense.ApprovalStatus.REJECTED);
        expenseRepository.save(expense);

        CancelledExpenseLog log = new CancelledExpenseLog();
        log.setOtherExpense(expense);
        log.setCancelledBy(request.getCancelledBy());
        log.setCancelReason(request.getCancelReason());
        log.setOldAmount(expense.getAmount());
        log.setExpenseCategory(expense.getExpenseCategory());
        cancelledExpenseLogRepository.save(log);

        OtherExpenseReturn expenseReturn = new OtherExpenseReturn();
        expenseReturn.setOtherExpense(expense);
        expenseReturn.setReturnDate(LocalDate.now());
        expenseReturn.setReturnedAmount(expense.getAmount());
        expenseReturn.setRefundMode(expense.getPaymentMode().toString());
        expenseReturn.setReturnedBy(expense.getPaidTo());
        expenseReturn.setReturnReason("Cancelled: " + request.getCancelReason());
        expenseReturn.setReceivedBy(request.getCancelledBy());


        expenseReturnRepository.save(expenseReturn);
    }
    @Override
    public List<OtherExpenseResponse> generateExpenseReport(ReportRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start Date and End Date are required.");
        }

        return expenseRepository.findByExpenseDateBetween(request.getStartDate(), request.getEndDate()).stream()
                .map(OtherExpenseResponse::fromEntity)
                .collect(Collectors.toList());
    }
}