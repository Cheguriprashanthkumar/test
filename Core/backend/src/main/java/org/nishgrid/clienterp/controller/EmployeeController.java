package org.nishgrid.clienterp.controller;

import jakarta.validation.Valid;
import org.nishgrid.clienterp.dto.ApiResponse;
import org.nishgrid.clienterp.dto.EmployeeIdCardDTO;
import org.nishgrid.clienterp.dto.EmployeeRegistrationRequest;
import org.nishgrid.clienterp.dto.EmployeeSelectionDTO;
import org.nishgrid.clienterp.model.Employee;
import org.nishgrid.clienterp.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Employee>> registerEmployee(@Valid @RequestBody EmployeeRegistrationRequest request) {
        Employee newEmployee = employeeService.registerNewEmployee(request);
        ApiResponse<Employee> response = new ApiResponse<>(true, "Employee registered successfully", newEmployee);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/selection")
    public ResponseEntity<List<EmployeeSelectionDTO>> getAllEmployeesForSelection() {
        List<EmployeeSelectionDTO> employeeList = employeeService.getAllEmployeesForSelection();
        return ResponseEntity.ok(employeeList);
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployeeById(@PathVariable("id") Integer id) {
        Employee employee = employeeService.findEmployeeById(id);
        ApiResponse<Employee> response = new ApiResponse<>(true, "Employee found", employee);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-code/{employeeCode}")
    public ResponseEntity<ApiResponse<Employee>> getEmployeeByCode(@PathVariable("employeeCode") String employeeCode) {
        Employee employee = employeeService.findEmployeeByCode(employeeCode);
        ApiResponse<Employee> response = new ApiResponse<>(true, "Employee found", employee);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id-card/{employeeCode}")
    public ResponseEntity<ApiResponse<EmployeeIdCardDTO>> getEmployeeIdCardDetails(@PathVariable("employeeCode") String employeeCode) {
        EmployeeIdCardDTO idCardDTO = employeeService.getEmployeeIdCardData(employeeCode);
        ApiResponse<EmployeeIdCardDTO> response = new ApiResponse<>(true, "ID card data retrieved", idCardDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployees(@RequestParam("query") String query) {
        List<Employee> employees = employeeService.searchEmployees(query);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(@PathVariable("id") Integer id, @Valid @RequestBody EmployeeRegistrationRequest request) {
        Employee updatedEmployee = employeeService.updateEmployee(id, request);
        ApiResponse<Employee> response = new ApiResponse<>(true, "Employee updated successfully", updatedEmployee);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable("id") Integer id) {
        employeeService.deleteEmployee(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Employee with ID: " + id + " deleted successfully.");
        return ResponseEntity.ok(response);
    }
}