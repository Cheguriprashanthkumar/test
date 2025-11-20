package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee_master")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;

    @NotBlank(message = "Employee code is required")
    @Column(unique = true)
    private String employeeCode;
    private BigDecimal salary;

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @NotNull(message = "Date of joining is required")
    private LocalDate doj;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Primary mobile number is required")
    @Digits(integer = 10, fraction = 0, message = "Mobile number must be 10 digits")
    private Long mobileNumber1;

    @Digits(integer = 10, fraction = 0, message = "Mobile number must be 10 digits")
    private Long mobileNumber2;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Digits(integer = 12, fraction = 0, message = "Aadhaar number must be 12 digits")
    private Long aadhaarNumber;

    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN card number")
    private String panCardNumber;

    private String aadhaarCardDocument;
    private String panCardDocument;
    private String address;

    @NotBlank(message = "Status is required")
    private String status;

    private String photo;
    private String fingerPrint;



    public String getPanCardDocument() {
        return panCardDocument;
    }

    public void setPanCardDocument(String panCardDocument) {
        this.panCardDocument = panCardDocument;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public LocalDate getDoj() {
        return doj;
    }

    public void setDoj(LocalDate doj) {
        this.doj = doj;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Long getMobileNumber1() {
        return mobileNumber1;
    }

    public void setMobileNumber1(Long mobileNumber1) {
        this.mobileNumber1 = mobileNumber1;
    }

    public Long getMobileNumber2() {
        return mobileNumber2;
    }

    public void setMobileNumber2(Long mobileNumber2) {
        this.mobileNumber2 = mobileNumber2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAadhaarCardDocument() {
        return aadhaarCardDocument;
    }

    public void setAadhaarCardDocument(String aadhaarCardDocument) {
        this.aadhaarCardDocument = aadhaarCardDocument;
    }

    public String getPanCardNumber() {
        return panCardNumber;
    }

    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }

    public Long getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(Long aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }
}
