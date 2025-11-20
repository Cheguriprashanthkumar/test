package org.nishgrid.clienterp.dto;

import java.time.LocalDateTime;


public class DevicePunchRequest {
    private String employeeCode;
    private LocalDateTime punchTime;

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDateTime getPunchTime() {
        return punchTime;
    }

    public void setPunchTime(LocalDateTime punchTime) {
        this.punchTime = punchTime;
    }
}