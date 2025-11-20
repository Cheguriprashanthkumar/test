package org.nishgrid.clienterp.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SaleUpdateDTO {
    private LocalDate invoiceDate;
    private String paymentMode;
    private double discount;
    private double gstPercent;
    private String status;
    private String remarks;
}