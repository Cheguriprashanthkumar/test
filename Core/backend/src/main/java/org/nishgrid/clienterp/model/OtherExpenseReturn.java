package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "other_expense_returns")
@Getter
@Setter
public class OtherExpenseReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnId;

    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private OtherExpense otherExpense;

    private LocalDate returnDate;
    private BigDecimal returnedAmount;
    private String refundMode;
    private String refundReferenceNo;
    private String returnedBy;
    @Lob private String returnReason;
    private String receivedBy;
    @Lob private String remarks;
    private String attachmentPath;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdOn;
    private String approvedBy;
}