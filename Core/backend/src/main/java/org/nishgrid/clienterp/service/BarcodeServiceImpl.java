package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.BarcodeRequest;
import org.nishgrid.clienterp.dto.BarcodeResponse;
import org.nishgrid.clienterp.model.Barcode;
import org.nishgrid.clienterp.model.GoodsReceiptNote;
import org.nishgrid.clienterp.repository.BarcodeRepository;
import org.nishgrid.clienterp.repository.GrnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BarcodeServiceImpl implements BarcodeService {

    @Autowired private BarcodeRepository barcodeRepository;
    @Autowired private GrnRepository grnRepository;

    @Override
    public List<BarcodeResponse> createBarcodes(BarcodeRequest request) {
        GoodsReceiptNote grn = grnRepository.findByIdWithDetails(request.getGrnId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GRN not found"));

        List<Barcode> barcodesToSave = request.getBarcodes().stream().map(itemDto -> {
            Barcode barcode = new Barcode();
            barcode.setGrn(grn);
            barcode.setItemName(itemDto.getItemName());
            barcode.setBarcodeNo(itemDto.getBarcodeNo());
            barcode.setWeight(itemDto.getWeight());
            barcode.setScannedBy(request.getScannedBy());
            return barcode;
        }).collect(Collectors.toList());

        List<Barcode> savedBarcodes = barcodeRepository.saveAll(barcodesToSave);

        return savedBarcodes.stream()
                .map(BarcodeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<BarcodeResponse> getBarcodesByGrnId(Long grnId) {
        return barcodeRepository.findByGrnId(grnId).stream()
                .map(BarcodeResponse::fromEntity)
                .collect(Collectors.toList());
    }
}