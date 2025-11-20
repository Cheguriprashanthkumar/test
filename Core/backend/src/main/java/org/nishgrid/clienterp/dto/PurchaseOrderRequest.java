package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderRequest {
    private String poNumber;
    private Long vendorId;
    private LocalDate orderDate;
    private String remarks;
    private BigDecimal totalAmount;
    private List<PurchaseOrderItemDTO> items;

    @Data
    public static class PurchaseOrderItemDTO {
        private String productName;
        private String purity;
        private BigDecimal weight;
        private BigDecimal ratePerUnit;
        private BigDecimal taxPercent;
        private BigDecimal totalPrice;
    }
}