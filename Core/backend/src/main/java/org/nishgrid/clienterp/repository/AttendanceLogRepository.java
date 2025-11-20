package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Integer> {

    List<AttendanceLog> findByEmployeeEmployeeCode(String employeeCode);

    AttendanceLog findByEmployeeEmployeeCodeAndDate(String employeeCode, LocalDate date);

    List<AttendanceLog> findByDate(LocalDate date);
}
