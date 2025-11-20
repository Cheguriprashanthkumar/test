package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.VendorPaymentRequest;
import org.nishgrid.clienterp.dto.VendorPaymentResponse;
import java.util.List;

public interface VendorPaymentService {
    VendorPaymentResponse createPayment(VendorPaymentRequest request);
    List<VendorPaymentResponse> getAllPayments();
    VendorPaymentResponse getPaymentById(Long id);
    VendorPaymentResponse updatePayment(Long id, VendorPaymentRequest request);
    void deletePayment(Long id);
}