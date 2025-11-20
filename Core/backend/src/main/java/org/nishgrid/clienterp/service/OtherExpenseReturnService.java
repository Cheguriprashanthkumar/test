package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.OtherExpenseReturnRequest;
import org.nishgrid.clienterp.dto.OtherExpenseReturnResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OtherExpenseReturnService {
    OtherExpenseReturnResponse createReturn(OtherExpenseReturnRequest request, MultipartFile file);
    List<OtherExpenseReturnResponse> getAllReturns();
    OtherExpenseReturnResponse getReturnById(Long id);
    OtherExpenseReturnResponse updateReturn(Long id, OtherExpenseReturnRequest request, MultipartFile file);
    void deleteReturn(Long id);
}