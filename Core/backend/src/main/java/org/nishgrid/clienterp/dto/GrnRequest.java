package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GrnRequest {
    private String grnNumber;
    private Long purchaseOrderId;
    private LocalDate receivedDate;
    private String receivedBy;
    private String remarks;
}