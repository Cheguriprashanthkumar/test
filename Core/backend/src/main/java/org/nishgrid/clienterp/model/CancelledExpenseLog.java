package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cancelled_expense_log")
@Getter
@Setter
public class CancelledExpenseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private OtherExpense otherExpense;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime cancelledOn;

    private String cancelledBy;
    @Lob private String cancelReason;
    private BigDecimal oldAmount;
    private String expenseCategory;
}