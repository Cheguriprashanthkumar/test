package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.Objects;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesInvoiceResponse {
    private Long invoiceId;
    private String invoiceNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesInvoiceResponse that = (SalesInvoiceResponse) o;
        return Objects.equals(invoiceId, that.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId);
    }
}