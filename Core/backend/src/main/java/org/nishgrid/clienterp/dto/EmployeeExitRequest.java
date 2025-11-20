package org.nishgrid.clienterp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeExitRequest {
    private String employeeCode;
    private String exitDate;
    private String exitReason;
    private BigDecimal finalSettlement;
    private String feedbackNotes;
    private String clearanceStatus;
}
