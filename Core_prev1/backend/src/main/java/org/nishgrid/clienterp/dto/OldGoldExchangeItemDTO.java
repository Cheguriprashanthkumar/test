package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

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