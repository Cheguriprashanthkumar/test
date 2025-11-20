package org.nishgrid.clienterp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesListResponse {
    private List<SalesRecordDto> salesRecords;
    private BigDecimal totalSalesAmount;
}