package org.nishgrid.clienterp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySummaryDTO {
    private Double totalSales;
    private Double totalTax;
    private Double totalDiscount;
    private Long totalItemsSold;
    private Long totalCustomers;
}