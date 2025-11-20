package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeFx {
    private Integer employeeId;
    private String employeeCode;
    private String fullName;
    private String gender;
    private LocalDate dob;
    private LocalDate doj;
    private String department;
    private String designation;
    private Long mobileNumber1;
    private Long mobileNumber2;
    private String email;
    private Long aadhaarNumber;
    private String panCardNumber;
    private String aadhaarCardDocument;
    private String panCardDocument;
    private String address;
    private String status;
    private String photo;
    private String fingerPrint;
    private BigDecimal salary;

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public Long getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(Long aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPanCardNumber() {
        return panCardNumber;
    }

    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }

    public String getAadhaarCardDocument() {
        return aadhaarCardDocument;
    }

    public void setAadhaarCardDocument(String aadhaarCardDocument) {
        this.aadhaarCardDocument = aadhaarCardDocument;
    }

    public String getPanCardDocument() {
        return panCardDocument;
    }

    public void setPanCardDocument(String panCardDocument) {
        this.panCardDocument = panCardDocument;
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
// Getters and setters for all fields (generate or copy from backend)
}
