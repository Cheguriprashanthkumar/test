package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.model.EmployeeExit;
import org.nishgrid.clienterp.dto.EmployeeExitRequest;
import org.nishgrid.clienterp.service.EmployeeExitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exits")
@RequiredArgsConstructor
public class EmployeeExitController {

    private final EmployeeExitService service;

    @PostMapping
    public ResponseEntity<?> recordExit(@RequestBody EmployeeExitRequest request) {
        String message = service.recordExit(request);
        return ResponseEntity.ok().body(message);
    }

    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<EmployeeExit> getExitByEmployeeCode(@PathVariable("employeeCode") String employeeCode) {
        return ResponseEntity.ok(service.getExitByEmployeeCode(employeeCode));
    }
    @GetMapping
    public ResponseEntity<List<EmployeeExit>> getAllExits() {
        return ResponseEntity.ok(service.getAllExits());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeExit>> getExitsByClearanceStatus(@PathVariable("status") String status) {
        return ResponseEntity.ok(service.getExitsByClearanceStatus(status));
    }

    @GetMapping("/after/{date}")
    public ResponseEntity<List<EmployeeExit>> getExitsAfterDate(@PathVariable("date") String date) {
        return ResponseEntity.ok(service.getExitsAfterDate(date));
    }

    @GetMapping("/before/{date}")
    public ResponseEntity<List<EmployeeExit>> getExitsBeforeDate(@PathVariable("date") String date) {
        return ResponseEntity.ok(service.getExitsBeforeDate(date));
    }

    @PutMapping("/{exitId}/status")
    public ResponseEntity<String> updateClearanceStatus(@PathVariable("exitId") Integer exitId,
                                                        @RequestParam("status") String status) {
        return ResponseEntity.ok(service.updateClearanceStatus(exitId, status));
    }
    @PutMapping("/{exitId}")
    public ResponseEntity<String> updateExit(@PathVariable("exitId") Integer exitId,
                                             @RequestBody EmployeeExit updatedExit) {
        updatedExit.setExitId(exitId); // ✅ Ensure the ID from the path is used
        service.updateExit(updatedExit);
        return ResponseEntity.ok("Exit updated successfully");
    }





}
