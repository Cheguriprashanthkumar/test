package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.VendorPaymentRequest;
import org.nishgrid.clienterp.dto.VendorPaymentResponse;
import org.nishgrid.clienterp.model.Vendor;
import org.nishgrid.clienterp.model.VendorPayment;
import org.nishgrid.clienterp.repository.VendorPaymentRepository;
import org.nishgrid.clienterp.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VendorPaymentServiceImpl implements VendorPaymentService {

    @Autowired private VendorPaymentRepository paymentRepository;
    @Autowired private VendorRepository vendorRepository;

    @Override
    public VendorPaymentResponse createPayment(VendorPaymentRequest request) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        VendorPayment payment = new VendorPayment();
        mapDtoToEntity(request, vendor, payment);

        VendorPayment savedPayment = paymentRepository.save(payment);
        return VendorPaymentResponse.fromEntity(savedPayment);
    }

    @Override
    public List<VendorPaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(VendorPaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public VendorPaymentResponse getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(VendorPaymentResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @Override
    public VendorPaymentResponse updatePayment(Long id, VendorPaymentRequest request) {
        VendorPayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        mapDtoToEntity(request, vendor, payment);

        VendorPayment updatedPayment = paymentRepository.save(payment);
        return VendorPaymentResponse.fromEntity(updatedPayment);
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
        }
        paymentRepository.deleteById(id);
    }

    private void mapDtoToEntity(VendorPaymentRequest request, Vendor vendor, VendorPayment payment) {
        payment.setVendor(vendor);
        payment.setPaymentDate(request.getPaymentDate());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentMode(request.getPaymentMode());
        payment.setReferenceNo(request.getReferenceNo());
        payment.setRemarks(request.getRemarks());
    }
}