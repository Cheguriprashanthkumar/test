package org.nishgrid.clienterp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeRegistrationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;
    private String gender;
    @NotNull(message = "Date of birth is required")
    private LocalDate dob;
    @NotNull(message = "Date of joining is required")
    private LocalDate doj;
    @NotBlank(message = "Department is required")
    private String department;
    private String designation;
    private String mobileNumber1;
    private String mobileNumber2;
    @Email(message = "A valid email is required")
    private String email;
    private String aadhaarNumber;
    private String panCardNumber;
    private String aadhaarCardDocument;
    private String panCardDocument;
    private String address;
    private String status;
    private String photo;
    private String fingerPrint;

    @NotNull(message = "Basic salary is required")
    @PositiveOrZero(message = "Basic salary must be zero or positive")
    private BigDecimal basicSalary;

    @NotNull(message = "HRA is required")
    @PositiveOrZero(message = "HRA must be zero or positive")
    private BigDecimal hra;

    @NotNull(message = "Other allowances are required")
    @PositiveOrZero(message = "Allowances must be zero or positive")
    private BigDecimal otherAllowances;

    @NotNull(message = "PF deduction is required")
    @PositiveOrZero(message = "PF must be zero or positive")
    private BigDecimal pfDeduction;

    @NotNull(message = "ESI deduction is required")
    @PositiveOrZero(message = "ESI must be zero or positive")
    private BigDecimal esiDeduction;

    @NotNull(message = "TDS deduction is required")
    @PositiveOrZero(message = "TDS must be zero or positive")
    private BigDecimal tdsDeduction;


}