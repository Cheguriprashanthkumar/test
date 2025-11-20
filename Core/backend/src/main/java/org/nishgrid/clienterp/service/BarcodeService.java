package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.BarcodeRequest;
import org.nishgrid.clienterp.dto.BarcodeResponse;
import java.util.List;

public interface BarcodeService {
    List<BarcodeResponse> createBarcodes(BarcodeRequest request);
    List<BarcodeResponse> getBarcodesByGrnId(Long grnId);
}