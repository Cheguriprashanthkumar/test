package org.nishgrid.clienterp.service;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.AttendanceLogRequest;
import org.nishgrid.clienterp.dto.DevicePunchRequest;
import org.nishgrid.clienterp.model.AttendanceLog;
import org.nishgrid.clienterp.model.Employee;
import org.nishgrid.clienterp.repository.AttendanceLogRepository;
import org.nishgrid.clienterp.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceLogService {

    private final AttendanceLogRepository attendanceLogRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceLog saveAttendanceLog(AttendanceLogRequest request) {
        Employee employee = employeeRepository.findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Employee not found with code: " + request.getEmployeeCode()));

        AttendanceLog log = new AttendanceLog();
        log.setEmployee(employee);
        log.setDate(LocalDate.parse(request.getDate()));
        log.setCheckInTime(LocalTime.parse(request.getCheckInTime()));
        log.setCheckOutTime(LocalTime.parse(request.getCheckOutTime()));
        log.setPresentStatus(request.getPresentStatus());

        BigDecimal hours = BigDecimal.valueOf(Duration.between(
                log.getCheckInTime(), log.getCheckOutTime()).toHours());
        log.setWorkingHours(hours);

        return attendanceLogRepository.save(log);
    }

    public List<AttendanceLog> getLogsByEmployeeCode(String employeeCode) {
        return attendanceLogRepository.findByEmployeeEmployeeCode(employeeCode);
    }

    public List<AttendanceLog> getLogsByDate(LocalDate date) {
        return attendanceLogRepository.findByDate(date);
    }

    public List<AttendanceLog> getLogsByEmployeeAndDate(String employeeCode, LocalDate date) {
        AttendanceLog log = attendanceLogRepository.findByEmployeeEmployeeCodeAndDate(employeeCode, date);
        return log != null ? List.of(log) : List.of();
    }


    public AttendanceLog updateAttendanceLog(AttendanceLogRequest request) {
        String employeeCode = request.getEmployeeCode();
        LocalDate date = LocalDate.parse(request.getDate());

        AttendanceLog log = attendanceLogRepository
                .findByEmployeeEmployeeCodeAndDate(employeeCode, date);

        if (log == null) {
            throw new RuntimeException("Attendance log not found for employeeCode: " + employeeCode + " on date: " + date);
        }

        if (request.getCheckInTime() != null) {
            log.setCheckInTime(LocalTime.parse(request.getCheckInTime()));
        }

        if (request.getCheckOutTime() != null) {
            log.setCheckOutTime(LocalTime.parse(request.getCheckOutTime()));
        }

        if (request.getPresentStatus() != null) {
            log.setPresentStatus(request.getPresentStatus());
        }

        // Only calculate working hours if both times are available
        if (log.getCheckInTime() != null && log.getCheckOutTime() != null) {
            BigDecimal hours = BigDecimal.valueOf(
                    Duration.between(log.getCheckInTime(), log.getCheckOutTime()).toHours()
            );
            log.setWorkingHours(hours);
        }

        return attendanceLogRepository.save(log);
    }

    public List<AttendanceLog> getAllLogs() {
        return attendanceLogRepository.findAll();
    }


    public AttendanceLog updateByEmployeeCode(String employeeCode, AttendanceLogRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());

        AttendanceLog log = attendanceLogRepository
                .findByEmployeeEmployeeCodeAndDate(employeeCode, date);

        if (log == null) {
            throw new RuntimeException("Attendance log not found for employeeCode: " + employeeCode + " on date: " + date);
        }

        log.setCheckInTime(LocalTime.parse(request.getCheckInTime()));
        log.setCheckOutTime(LocalTime.parse(request.getCheckOutTime()));
        log.setPresentStatus(request.getPresentStatus());

        BigDecimal hours = BigDecimal.valueOf(
                Duration.between(log.getCheckInTime(), log.getCheckOutTime()).toHours()
        );
        log.setWorkingHours(hours);

        return attendanceLogRepository.save(log);
    }
    // In AttendanceLogService.java

    public AttendanceLog processDevicePunch(DevicePunchRequest request) {
        Employee employee = employeeRepository.findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Employee not found with code: " + request.getEmployeeCode()));

        LocalDate punchDate = request.getPunchTime().toLocalDate();
        LocalTime punchTime = request.getPunchTime().toLocalTime();

        // Find if a log for this employee on this date already exists
        AttendanceLog log = attendanceLogRepository
                .findByEmployeeEmployeeCodeAndDate(request.getEmployeeCode(), punchDate);

        if (log == null) {
            // First punch of the day: It's a Check-in
            log = new AttendanceLog();
            log.setEmployee(employee);
            log.setDate(punchDate);
            log.setCheckInTime(punchTime);
            log.setPresentStatus("Present"); // Set initial status
        } else {
            // Subsequent punch of the day: It's a Check-out
            log.setCheckOutTime(punchTime);
            // Recalculate working hours
            if (log.getCheckInTime() != null) {
                Duration duration = Duration.between(log.getCheckInTime(), log.getCheckOutTime());
                BigDecimal hours = BigDecimal.valueOf(duration.toMinutes() / 60.0)
                        .setScale(2, RoundingMode.HALF_UP);
                log.setWorkingHours(hours);
            }
        }
        return attendanceLogRepository.save(log);
    }
}
