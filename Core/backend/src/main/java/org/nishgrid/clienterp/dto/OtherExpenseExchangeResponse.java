package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.OtherExpenseExchange;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OtherExpenseExchangeResponse {
    private Long exchangeId;
    private Long oldExpenseId;
    private String oldExpenseCategory;
    private Long newExpenseId;
    private String newExpenseCategory;
    private LocalDate exchangeDate;
    private Long vendorId;
    private String vendorName;
    private String reason;
    private BigDecimal adjustedAmount;
    private String approvedBy;
    private String createdBy;
    private String remarks;

    public static OtherExpenseExchangeResponse fromEntity(OtherExpenseExchange exchange) {
        OtherExpenseExchangeResponse dto = new OtherExpenseExchangeResponse();
        dto.setExchangeId(exchange.getExchangeId());
        dto.setOldExpenseId(exchange.getOldExpense().getExpenseId());
        dto.setOldExpenseCategory(exchange.getOldExpense().getExpenseCategory());
        dto.setNewExpenseId(exchange.getNewExpense().getExpenseId());
        dto.setNewExpenseCategory(exchange.getNewExpense().getExpenseCategory());
        dto.setExchangeDate(exchange.getExchangeDate());
        if (exchange.getVendor() != null) {
            dto.setVendorId(exchange.getVendor().getId());
            dto.setVendorName(exchange.getVendor().getName());
        }
        dto.setReason(exchange.getReason());
        dto.setAdjustedAmount(exchange.getAdjustedAmount());
        dto.setApprovedBy(exchange.getApprovedBy());
        dto.setCreatedBy(exchange.getCreatedBy());
        dto.setRemarks(exchange.getRemarks());
        return dto;
    }
}