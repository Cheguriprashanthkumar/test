package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    // Updated from employeeId to employeeCode
    List<LeaveRequest> findByEmployeeEmployeeCode(String employeeCode);

    List<LeaveRequest> findByStatus(String status);

    List<LeaveRequest> findByFromDateGreaterThanEqualAndToDateLessThanEqual(LocalDate from, LocalDate to);

    List<LeaveRequest> findByStatusOrderByAppliedOnDesc(String status);
}
