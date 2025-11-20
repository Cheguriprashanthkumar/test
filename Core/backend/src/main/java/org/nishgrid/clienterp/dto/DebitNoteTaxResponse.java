package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.DebitNoteTax;

import java.math.BigDecimal;

@Data
public class DebitNoteTaxResponse {
    private Long id;
    private DebitNoteTax.TaxType taxType;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    public static DebitNoteTaxResponse fromEntity(DebitNoteTax tax) {
        DebitNoteTaxResponse dto = new DebitNoteTaxResponse();
        dto.setId(tax.getId());
        dto.setTaxType(tax.getTaxType());
        dto.setTaxRate(tax.getTaxRate());
        dto.setTaxAmount(tax.getTaxAmount());
        return dto;
    }
}