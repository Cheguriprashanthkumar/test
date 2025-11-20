package org.nishgrid.clienterp.dto;

import lombok.Data;

@Data
public class AttendanceLogRequest {
    private String employeeCode;
    private String date;         // Format: yyyy-MM-dd
    private String checkInTime;  // Format: HH:mm:ss
    private String checkOutTime; // Format: HH:mm:ss
    private String presentStatus;
}
