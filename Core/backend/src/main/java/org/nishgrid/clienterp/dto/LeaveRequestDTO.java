package org.nishgrid.clienterp.dto;

import lombok.Data;

@Data
public class LeaveRequestDTO {
    private String employeeCode;
    private String leaveType;
    private String holidayName;
    private String fromDate;
    private String toDate;
    private Integer totalDays;
    private String approvedBy;
    private String leavePaymentType;
}
