package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesSummaryReport {

    public enum ReportType {
        DAILY,
        MONTHLY,
        YEARLY,
        CUSTOM,
        FINANCIAL_YEAR
    }

    private Long reportId;
    private LocalDate reportDate;
    private ReportType reportType;
    private BigDecimal totalSales;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private BigDecimal totalReturned;
    private int totalItemsSold;
    private int totalCustomers;
    private LocalDateTime generatedOn;

}