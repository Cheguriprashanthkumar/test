package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Integer> {

    Optional<SalaryStructure> findByEmployee_EmployeeCode(String employeeCode);

    void deleteByEmployee_EmployeeId(Integer employeeId);

    Optional<SalaryStructure> findByEmployee_EmployeeId(Integer id);
}