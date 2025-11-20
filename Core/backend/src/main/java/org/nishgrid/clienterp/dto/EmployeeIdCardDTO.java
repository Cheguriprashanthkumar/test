package org.nishgrid.clienterp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeIdCardDTO {
    private String employeeCode;
    private String fullName;
    private String designation;
    private String department;
    private String email;
    private String mobileNumber;
    private String photo;
    private String joiningDate;
}

