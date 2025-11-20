package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_items")
@Getter
@Setter
public class SalesItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salesItemId;

    private String itemName;
    private String hsnCode;
    private String purity;
    private Double grossWeight;
    private Double netWeight;
    private BigDecimal ratePerGram;

    private BigDecimal makingCharge;
    private BigDecimal makingChargeAmount;

    private BigDecimal diamondCarat;
    private BigDecimal diamondRate;
    private BigDecimal diamondAmount;

    private BigDecimal totalPrice;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private SalesInvoice invoice;

    public String getName() {
        return itemName;
    }

    public int getQuantity() {
        return netWeight != null ? netWeight.intValue() : 0;
    }
}