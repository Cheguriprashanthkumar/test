package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "other_expense_exchanges")
@Getter
@Setter
public class OtherExpenseExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exchangeId;

    @ManyToOne
    @JoinColumn(name = "old_expense_id", nullable = false)
    private OtherExpense oldExpense;

    @ManyToOne
    @JoinColumn(name = "new_expense_id", nullable = false)
    private OtherExpense newExpense;

    private LocalDate exchangeDate;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Lob
    private String reason;

    private BigDecimal adjustedAmount;
    private String approvedBy;
    private String createdBy;
    @Lob private String remarks;
}