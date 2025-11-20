package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.LeaveRequestDTO;
import org.nishgrid.clienterp.model.LeaveRequest;
import org.nishgrid.clienterp.service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService service;

    // POST: Apply leave
    @PostMapping
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRequestDTO request) {
        String message = service.applyLeave(request);
        return ResponseEntity.ok().body(message);
    }


    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(service.getAllLeaveRequests());
    }


    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<List<LeaveRequest>> getLeaveByEmployee(@PathVariable("employeeCode") String employeeCode) {
        return ResponseEntity.ok(service.getLeaveRequestsByEmployeeCode(employeeCode));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveRequest>> getLeaveByStatus(@PathVariable("status") String status) {
        return ResponseEntity.ok(service.getLeaveRequestsByStatus(status));
    }



    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateLeaveStatus(
            @PathVariable("id") Integer id,
            @RequestParam("status") String status) {
        return ResponseEntity.ok(service.updateLeaveStatus(id, status));
    }

}
