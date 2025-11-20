package org.nishgrid.clienterp.service;

import jakarta.transaction.Transactional;
import org.nishgrid.clienterp.dto.GiftVoucherCreateDTO;
import org.nishgrid.clienterp.exception.ResourceNotFoundException;
import org.nishgrid.clienterp.model.Customer;
import org.nishgrid.clienterp.model.GiftVoucher;
import org.nishgrid.clienterp.model.VoucherStatus;
import org.nishgrid.clienterp.repository.CustomerRepository;
import org.nishgrid.clienterp.repository.GiftVoucherRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class GiftVoucherService {

    private final GiftVoucherRepository voucherRepository;
    private final CustomerRepository customerRepository;

    public GiftVoucherService(GiftVoucherRepository voucherRepository, CustomerRepository customerRepository) {
        this.voucherRepository = voucherRepository;
        this.customerRepository = customerRepository;
    }

    public List<GiftVoucher> findAllVouchers() {
        return voucherRepository.findAll();
    }

    @Transactional
    public GiftVoucher createVoucher(GiftVoucherCreateDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        GiftVoucher voucher = new GiftVoucher();
        voucher.setCustomer(customer);
        voucher.setValue(dto.getValue());
        voucher.setStatus(VoucherStatus.UNUSED);
        voucher.setVoucherCode("GV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return voucherRepository.save(voucher);
    }

    @Transactional
    public GiftVoucher updateVoucherStatus(Long id, String newStatusStr) {
        GiftVoucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (voucher.getStatus() == VoucherStatus.USED) {
            throw new IllegalStateException("This voucher has already been used and its status cannot be changed.");
        }

        VoucherStatus newStatus;
        try {
            newStatus = VoucherStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: '" + newStatusStr + "'. Must be 'USED' or 'UNUSED'.");
        }

        if (newStatus == VoucherStatus.UNUSED) {
            throw new IllegalStateException("Cannot change a voucher's status back to UNUSED.");
        }

        voucher.setStatus(newStatus);
        return voucherRepository.save(voucher);
    }

    public void deleteVoucher(Long id) {
        if (!voucherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Voucher not found with id: " + id);
        }
        voucherRepository.deleteById(id);
    }
}