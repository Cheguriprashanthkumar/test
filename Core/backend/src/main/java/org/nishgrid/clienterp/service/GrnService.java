package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.GrnRequest;
import org.nishgrid.clienterp.dto.GrnResponse;
import org.nishgrid.clienterp.model.GoodsReceiptNote;
import java.util.List;

public interface GrnService {

    GrnResponse createGrn(GrnRequest request);

    List<GoodsReceiptNote> getAllGrns();
    GrnResponse updateGrn(Long id, GrnRequest request);
    void deleteGrn(Long id);
}