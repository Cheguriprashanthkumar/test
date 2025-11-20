package org.nishgrid.clienterp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OldGoldExchangeItemDTO {
    private String itemName;
    private String metalType;
    private String purity;
    private Double grossWeight;
    private BigDecimal wastagePercent;
    private BigDecimal ratePerGram;
    private BigDecimal diamondCarat;
    private BigDecimal diamondRate;
    private BigDecimal deductionCharge;
    private Double netWeight;
    private BigDecimal totalItemValue;
}