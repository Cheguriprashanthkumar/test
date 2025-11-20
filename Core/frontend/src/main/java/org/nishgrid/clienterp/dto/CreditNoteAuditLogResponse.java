package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditNoteAuditLogResponse {
    private Long logId;
    private Long creditNoteId;
    private String changedBy;
    private String changeDescription;
    private LocalDateTime changedAt;
}