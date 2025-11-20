package org.nishgrid.clienterp.model;

public class AttendanceUpdateRequest {
    private String employeeCode;
    private String date;
    private String checkInTime;
    private String checkOutTime;
    private String remarks;

    public AttendanceUpdateRequest() {}

    public AttendanceUpdateRequest(String employeeCode, String date, String checkInTime, String checkOutTime, String remarks) {
        this.employeeCode = employeeCode;
        this.date = date;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.remarks = remarks;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
