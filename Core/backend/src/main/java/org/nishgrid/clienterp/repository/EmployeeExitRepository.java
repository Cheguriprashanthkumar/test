package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.EmployeeExit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeExitRepository extends JpaRepository<EmployeeExit, Integer> {


//    EmployeeExit findByEmployeeEmployeeId(Integer employeeId);
    EmployeeExit findByEmployeeEmployeeCode(String employeeCode);


    List<EmployeeExit> findByClearanceStatus(String clearanceStatus);


    List<EmployeeExit> findByExitDateAfter(java.time.LocalDate date);


    List<EmployeeExit> findByExitDateBefore(java.time.LocalDate date);
}
