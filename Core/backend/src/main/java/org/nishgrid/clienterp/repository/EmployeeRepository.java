package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByEmployeeCode(String employeeCode);
    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Employee> searchEmployees(@Param("query") String query);
    boolean existsByEmployeeCode(String employeeCode);
}
