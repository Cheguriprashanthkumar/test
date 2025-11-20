package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemDTO {
    private String itemName;
    private String hsnCode;
    private String purity;
    private double grossWeight;
    private double netWeight;
    private BigDecimal ratePerGram;
    private BigDecimal makingCharge;
    private BigDecimal makingChargeAmount;
    private BigDecimal diamondCarat;
    private BigDecimal diamondRate;
}