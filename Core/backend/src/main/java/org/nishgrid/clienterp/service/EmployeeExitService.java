package org.nishgrid.clienterp.service;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.EmployeeExitRequest;
import org.nishgrid.clienterp.model.Employee;
import org.nishgrid.clienterp.model.EmployeeExit;
import org.nishgrid.clienterp.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.nishgrid.clienterp.repository.EmployeeExitRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeExitService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeExitRepository exitRepository;

    public String recordExit(EmployeeExitRequest request) {
        Employee employee = employeeRepository.findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Employee not found with code: " + request.getEmployeeCode()));

        EmployeeExit exit = new EmployeeExit();
        exit.setEmployee(employee);
        exit.setExitDate(LocalDate.parse(request.getExitDate()));
        exit.setExitReason(request.getExitReason());
        exit.setFinalSettlement(request.getFinalSettlement());
        exit.setFeedbackNotes(request.getFeedbackNotes());
        exit.setClearanceStatus(request.getClearanceStatus());

        exitRepository.save(exit);

        return "Exit recorded for employee: " + employee.getFullName();
    }
    public EmployeeExit getExitByEmployeeCode(String employeeCode) {
        return exitRepository.findByEmployeeEmployeeCode(employeeCode);
    }

    public List<EmployeeExit> getExitsByClearanceStatus(String status) {
        return exitRepository.findByClearanceStatus(status);
    }

    public List<EmployeeExit> getExitsAfterDate(String date) {
        return exitRepository.findByExitDateAfter(LocalDate.parse(date));
    }

    public List<EmployeeExit> getExitsBeforeDate(String date) {
        return exitRepository.findByExitDateBefore(LocalDate.parse(date));
    }

    public String updateClearanceStatus(Integer exitId, String status) {
        EmployeeExit exit = exitRepository.findById(exitId)
                .orElseThrow(() -> new RuntimeException("Exit record not found with ID: " + exitId));
        exit.setClearanceStatus(status);
        exitRepository.save(exit);
        return "Clearance status updated for exit ID: " + exitId;
    }
    public List<EmployeeExit> getAllExits() {
        return exitRepository.findAll();
    }
    public void updateExit(EmployeeExit updatedExit) {
        EmployeeExit existing = exitRepository.findById(updatedExit.getExitId())
                .orElseThrow(() -> new RuntimeException("Exit not found"));

        // Ensure employee reference is updated if changed
        if (updatedExit.getEmployee() != null) {
            existing.setEmployee(updatedExit.getEmployee());
        }

        existing.setExitDate(updatedExit.getExitDate());
        existing.setExitReason(updatedExit.getExitReason());
        existing.setFinalSettlement(updatedExit.getFinalSettlement());
        existing.setFeedbackNotes(updatedExit.getFeedbackNotes());
        existing.setClearanceStatus(updatedExit.getClearanceStatus());

        exitRepository.save(existing);
    }


}
