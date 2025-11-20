package org.nishgrid.clienterp.dto;

import lombok.Data;

@Data
public class CancelExpenseRequest {
    private String cancelledBy;
    private String cancelReason;
}