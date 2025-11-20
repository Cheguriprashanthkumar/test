package org.nishgrid.clienterp.dto;

public class InvoiceSelectionDTO {
    private Long id;
    private String invoiceNo;

    public InvoiceSelectionDTO(Long id, String invoiceNo) {
        this.id = id;
        this.invoiceNo = invoiceNo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
}