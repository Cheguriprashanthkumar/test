package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Import this
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesItemDto {

    private String itemName;
    private String purity;
    private Double netWeight;
    private BigDecimal ratePerGram;
    private BigDecimal makingCharge; // For percentage
    private BigDecimal makingChargeAmount;

    private BigDecimal totalPrice;
}