package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PayslipRequest {
    private String employeeCode;
    private String month;
    private Integer totalWorkingDays;
    private Integer daysPresent;
    private BigDecimal totalSalary;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
}