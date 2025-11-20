package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.dto.SalesCommissionDTO;
import org.nishgrid.clienterp.exception.ResourceNotFoundException;
import org.nishgrid.clienterp.model.Employee;
import org.nishgrid.clienterp.model.SalesCommission;
import org.nishgrid.clienterp.model.SalesInvoice;
import org.nishgrid.clienterp.repository.EmployeeRepository;
import org.nishgrid.clienterp.repository.SalesCommissionRepository;
import org.nishgrid.clienterp.repository.SalesInvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommissionService {

    @Autowired private SalesCommissionRepository commissionRepo;
    @Autowired private EmployeeRepository employeeRepo;
    @Autowired private SalesInvoiceRepository invoiceRepo;

    public List<SalesCommission> getAll() {
        return commissionRepo.findAll();
    }

    public SalesCommission create(SalesCommissionDTO dto) {
        Employee employee = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));
        SalesInvoice invoice = invoiceRepo.findById(dto.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + dto.getInvoiceId()));

        SalesCommission commission = new SalesCommission();
        commission.setEmployee(employee);
        commission.setSalesInvoice(invoice);
        commission.setCommissionRate(dto.getCommissionRate());
        commission.setAmountTotal(dto.getAmountTotal());

        return commissionRepo.save(commission);
    }

    public SalesCommission update(Long id, SalesCommissionDTO dto) {
        SalesCommission commission = commissionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + id));
        Employee employee = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));
        SalesInvoice invoice = invoiceRepo.findById(dto.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + dto.getInvoiceId()));

        commission.setEmployee(employee);
        commission.setSalesInvoice(invoice);
        commission.setCommissionRate(dto.getCommissionRate());
        commission.setAmountTotal(dto.getAmountTotal());

        return commissionRepo.save(commission);
    }

    public void delete(Long id) {
        commissionRepo.deleteById(id);
    }
}