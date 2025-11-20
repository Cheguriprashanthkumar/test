package org.nishgrid.clienterp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySummaryDTO {
    private BigDecimal totalSales;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private Long totalItemsSold;
    private Long totalCustomers;
}