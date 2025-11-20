package org.nishgrid.clienterp.dto;

import lombok.Data;
import org.nishgrid.clienterp.model.OtherExpenseReturn;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OtherExpenseReturnResponse {
    private Long returnId;
    private Long expenseId;
    private String expenseCategory;
    private LocalDate returnDate;
    private BigDecimal returnedAmount;
    private String refundMode;
    private String refundReferenceNo;
    private String returnedBy;
    private String returnReason;
    private String receivedBy;
    private String remarks;
    private String attachmentPath;
    private LocalDateTime createdOn;
    private String approvedBy;

    public static OtherExpenseReturnResponse fromEntity(OtherExpenseReturn expenseReturn) {
        OtherExpenseReturnResponse dto = new OtherExpenseReturnResponse();
        dto.setReturnId(expenseReturn.getReturnId());
        dto.setExpenseId(expenseReturn.getOtherExpense().getExpenseId());
        dto.setExpenseCategory(expenseReturn.getOtherExpense().getExpenseCategory());
        dto.setReturnDate(expenseReturn.getReturnDate());
        dto.setReturnedAmount(expenseReturn.getReturnedAmount());
        dto.setRefundMode(expenseReturn.getRefundMode());
        dto.setRefundReferenceNo(expenseReturn.getRefundReferenceNo());
        dto.setReturnedBy(expenseReturn.getReturnedBy());
        dto.setReturnReason(expenseReturn.getReturnReason());
        dto.setReceivedBy(expenseReturn.getReceivedBy());
        dto.setRemarks(expenseReturn.getRemarks());
        dto.setAttachmentPath(expenseReturn.getAttachmentPath());
        dto.setCreatedOn(expenseReturn.getCreatedOn());
        dto.setApprovedBy(expenseReturn.getApprovedBy());
        return dto;
    }
}