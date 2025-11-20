package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.PurchaseReturnRequest;
import org.nishgrid.clienterp.dto.PurchaseReturnResponse;
import java.util.List;

public interface PurchaseReturnService {
    PurchaseReturnResponse createReturn(PurchaseReturnRequest request);
    List<PurchaseReturnResponse> getAllReturns();
    PurchaseReturnResponse getReturnById(Long id);
    PurchaseReturnResponse updateReturn(Long id, PurchaseReturnRequest request);
    void deleteReturn(Long id);
}