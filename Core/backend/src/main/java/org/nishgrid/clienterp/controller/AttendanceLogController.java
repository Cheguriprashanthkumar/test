package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.AttendanceLogRequest;
import org.nishgrid.clienterp.dto.DevicePunchRequest;
import org.nishgrid.clienterp.model.AttendanceLog;
import org.nishgrid.clienterp.service.AttendanceLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceLogController {

    private final AttendanceLogService service;

    @PostMapping
    public ResponseEntity<AttendanceLog> createAttendanceLog(@RequestBody AttendanceLogRequest request) {
        AttendanceLog log = service.saveAttendanceLog(request);
        return ResponseEntity.ok(log);
    }
    @GetMapping
    public ResponseEntity<List<AttendanceLog>> getAllLogs() {
        return ResponseEntity.ok(service.getAllLogs());
    }

    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<List<AttendanceLog>> getByEmployeeCode(@PathVariable("employeeCode") String employeeCode) {
        return ResponseEntity.ok(service.getLogsByEmployeeCode(employeeCode));
    }


    @GetMapping("/date/{date}")
    public ResponseEntity<List<AttendanceLog>> getByDate(@PathVariable("date") String date) {
        return ResponseEntity.ok(service.getLogsByDate(LocalDate.parse(date)));
    }

    
    @GetMapping("/employee-date")
    public ResponseEntity<List<AttendanceLog>> getByEmployeeAndDate(
            @RequestParam(name = "employeeCode") String employeeCode,
            @RequestParam(name = "date") String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(service.getLogsByEmployeeAndDate(employeeCode, localDate));
    }

    @PutMapping
    public ResponseEntity<AttendanceLog> updateAttendanceLog(@RequestBody AttendanceLogRequest request) {
        AttendanceLog updated = service.updateAttendanceLog(request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/employee-code")
    public ResponseEntity<AttendanceLog> updateByEmployeeCode(@RequestParam(name = "employeeCode") String employeeCode,
                                                              @RequestBody AttendanceLogRequest request) {
        AttendanceLog updated = service.updateByEmployeeCode(employeeCode, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/punch")
    public ResponseEntity<AttendanceLog> processDevicePunch(@RequestBody DevicePunchRequest punchRequest) {
        AttendanceLog log = service.processDevicePunch(punchRequest);
        return ResponseEntity.ok(log);
    }
}
