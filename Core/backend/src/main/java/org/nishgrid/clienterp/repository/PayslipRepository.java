package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.Employee;
import org.nishgrid.clienterp.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, Integer> {

    List<Payslip> findByEmployee_EmployeeCode(String employeeCode);

    Optional<Payslip> findByEmployee_EmployeeCodeAndMonth(String employeeCode, String month);

    List<Payslip> findByMonth(String month);

    List<Payslip> findByPaymentStatus(String paymentStatus);
    // New method for searching
    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Employee> searchEmployees(@Param("query") String query);
}