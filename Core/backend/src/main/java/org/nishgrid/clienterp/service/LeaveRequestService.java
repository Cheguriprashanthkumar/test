package org.nishgrid.clienterp.service;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.LeaveRequestDTO;
import org.nishgrid.clienterp.model.Employee;
import org.nishgrid.clienterp.model.LeaveRequest;
import org.nishgrid.clienterp.repository.EmployeeRepository;
import org.nishgrid.clienterp.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public String applyLeave(LeaveRequestDTO request) {

        Employee employee = employeeRepository.findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Employee not found with code: " + request.getEmployeeCode()));

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(request.getLeaveType());
        leave.setHolidayName(request.getHolidayName());
        leave.setFromDate(LocalDate.parse(request.getFromDate()));
        leave.setToDate(LocalDate.parse(request.getToDate()));
        leave.setTotalDays(request.getTotalDays());
        leave.setAppliedOn(LocalDate.now());
        leave.setApprovedBy(request.getApprovedBy());
        leave.setLeavePaymentType(request.getLeavePaymentType());
        leave.setStatus("Pending");

        leaveRequestRepository.save(leave);

        return "Leave request submitted for employee: " + employee.getFullName();
    }
    // Add this below applyLeave method
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public List<LeaveRequest> getLeaveRequestsByEmployeeCode(String code) {
        return leaveRequestRepository.findByEmployeeEmployeeCode(code);
    }

    public List<LeaveRequest> getLeaveRequestsByStatus(String status) {
        return leaveRequestRepository.findByStatusOrderByAppliedOnDesc(status);
    }

    public String updateLeaveStatus(Integer id, String status) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave Request not found with ID: " + id));

        leave.setStatus(status);
        leaveRequestRepository.save(leave);

        return "Leave request with ID " + id + " has been updated to status: " + status;
    }

}
