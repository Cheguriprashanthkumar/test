package org.nishgrid.clienterp.service;

import jakarta.transaction.Transactional;
import org.nishgrid.clienterp.dto.EmployeeIdCardDTO;
import org.nishgrid.clienterp.dto.EmployeeRegistrationRequest;
import org.nishgrid.clienterp.dto.EmployeeSelectionDTO;
import org.nishgrid.clienterp.exception.ResourceNotFoundException;
import org.nishgrid.clienterp.model.Employee;
import org.nishgrid.clienterp.model.SalaryStructure;
import org.nishgrid.clienterp.repository.EmployeeRepository;
import org.nishgrid.clienterp.repository.SalaryStructureRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;

    public EmployeeService(EmployeeRepository employeeRepository, SalaryStructureRepository salaryStructureRepository) {
        this.employeeRepository = employeeRepository;
        this.salaryStructureRepository = salaryStructureRepository;
    }

    @Transactional
    public Employee registerNewEmployee(EmployeeRegistrationRequest request) {
        String employeeCode = generateNextEmployeeCode();
        if (employeeRepository.existsByEmployeeCode(employeeCode)) {
            throw new IllegalArgumentException("Generated Employee code already exists. Please try again.");
        }

        Employee employee = new Employee();
        mapDtoToEmployee(request, employee);
        employee.setEmployeeCode(employeeCode);

        Employee savedEmployee = employeeRepository.save(employee);

        SalaryStructure salary = createSalaryStructure(request, savedEmployee);
        salaryStructureRepository.save(salary);

        return savedEmployee;
    }

    @Transactional
    public Employee updateEmployee(Integer id, EmployeeRegistrationRequest request) {
        Employee existingEmployee = findEmployeeById(id);
        mapDtoToEmployee(request, existingEmployee);

        SalaryStructure salary = salaryStructureRepository.findByEmployee_EmployeeId(id)
                .orElse(new SalaryStructure());

        updateSalaryStructure(salary, request, existingEmployee);
        salaryStructureRepository.save(salary);

        return employeeRepository.save(existingEmployee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<EmployeeSelectionDTO> getAllEmployeesForSelection() {
        return employeeRepository.findAll().stream()
                .map(emp -> new EmployeeSelectionDTO(emp.getEmployeeId(), emp.getFullName()))
                .collect(Collectors.toList());
    }

    public Employee findEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    public Employee findEmployeeByCode(String employeeCode) {
        return employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with code: " + employeeCode));
    }

    public List<Employee> searchEmployees(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return employeeRepository.searchEmployees(query);
    }

    public EmployeeIdCardDTO getEmployeeIdCardData(String employeeCode) {
        Employee employee = findEmployeeByCode(employeeCode);
        return new EmployeeIdCardDTO(
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getDesignation(),
                employee.getDepartment(),
                employee.getEmail(),
                String.valueOf(employee.getMobileNumber1()),
                employee.getPhoto(),
                employee.getDoj().toString()
        );
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }
        salaryStructureRepository.deleteByEmployee_EmployeeId(id);
        employeeRepository.deleteById(id);
    }

    private String generateNextEmployeeCode() {
        long count = employeeRepository.count() + 1;
        return String.format("Emp-%02d", count);
    }

    private void mapDtoToEmployee(EmployeeRegistrationRequest request, Employee employee) {
        employee.setFullName(request.getFullName());
        employee.setGender(request.getGender());
        employee.setDob(request.getDob());
        employee.setDoj(request.getDoj());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setMobileNumber1(Long.valueOf(request.getMobileNumber1()));
        employee.setMobileNumber2(Long.valueOf(request.getMobileNumber2()));
        employee.setEmail(request.getEmail());
        employee.setAadhaarNumber(Long.valueOf(request.getAadhaarNumber()));
        employee.setPanCardNumber(request.getPanCardNumber());
        employee.setAadhaarCardDocument(request.getAadhaarCardDocument());
        employee.setPanCardDocument(request.getPanCardDocument());
        employee.setAddress(request.getAddress());
        employee.setStatus(request.getStatus());
        employee.setPhoto(request.getPhoto());
        employee.setFingerPrint(request.getFingerPrint());
    }

    private SalaryStructure createSalaryStructure(EmployeeRegistrationRequest request, Employee employee) {
        SalaryStructure salary = new SalaryStructure();
        updateSalaryStructure(salary, request, employee);
        return salary;
    }

    private void updateSalaryStructure(SalaryStructure salary, EmployeeRegistrationRequest request, Employee employee) {
        BigDecimal totalSalary = request.getBasicSalary()
                .add(request.getHra())
                .add(request.getOtherAllowances())
                .subtract(request.getPfDeduction())
                .subtract(request.getEsiDeduction())
                .subtract(request.getTdsDeduction());

        salary.setEmployee(employee);
        salary.setBasicSalary(request.getBasicSalary());
        salary.setHra(request.getHra());
        salary.setOtherAllowances(request.getOtherAllowances());
        salary.setPfDeduction(request.getPfDeduction());
        salary.setEsiDeduction(request.getEsiDeduction());
        salary.setTdsDeduction(request.getTdsDeduction());
        salary.setTotalSalary(totalSalary);
        salary.setYearSalary(totalSalary.multiply(BigDecimal.valueOf(12)));
    }
}