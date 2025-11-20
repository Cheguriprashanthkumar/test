package org.nishgrid.clienterp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesListResponse {
    private List<SalesRecordDto> salesRecords;
    private BigDecimal totalSalesAmount;

    // This method calculates the sum of the 'Final Amount' from all SalesRecordDto objects.
    public BigDecimal getTotalFinalAmount() {
        if (salesRecords == null) {
            return BigDecimal.ZERO;
        }
        return salesRecords.stream()
                .map(dto -> Optional.ofNullable(dto.getFinalAmount()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}