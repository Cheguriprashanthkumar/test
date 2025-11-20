package org.nishgrid.clienterp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OldGoldExchangeRequestDTO {
    private String sellerName;
    private String sellerMobile;
    private String sellerEmail;
    private String sellerAddress;
    private String sellerGstin;
    private LocalDate purchaseDate;
    private String purchaseBillNo;
    private String payoutMode;
    private String otherPayoutMode;
    private String remarks;
    private List<OldGoldExchangeItemDTO> items;
    private BigDecimal totalPurchaseValue;
    private BigDecimal processingFeePercent;
    private BigDecimal processingFeeAmount;
    private BigDecimal netPayableAmount;
    private String status;
}