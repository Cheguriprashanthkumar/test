package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.CancelExpenseRequest;
import org.nishgrid.clienterp.dto.OtherExpenseRequest;
import org.nishgrid.clienterp.dto.OtherExpenseResponse;
import org.nishgrid.clienterp.dto.ReportRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface OtherExpenseService {
    OtherExpenseResponse createExpense(OtherExpenseRequest request, MultipartFile file);
    List<OtherExpenseResponse> getAllExpenses();
    OtherExpenseResponse getExpenseById(Long id);
    OtherExpenseResponse updateExpense(Long id, OtherExpenseRequest request, MultipartFile file);
    void deleteExpense(Long id);
    void cancelExpense(Long id, CancelExpenseRequest request);
    List<OtherExpenseResponse> generateExpenseReport(ReportRequest request);

}
