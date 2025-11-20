package org.nishgrid.clienterp.controller;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.CreditNoteAuditLogResponse;
import org.nishgrid.clienterp.service.CreditNoteAuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-note-audit-logs")
@RequiredArgsConstructor
public class CreditNoteAuditLogController {

    private final CreditNoteAuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<CreditNoteAuditLogResponse>> getAllAuditLogs() {
        List<CreditNoteAuditLogResponse> logs = auditLogService.getAllAuditLogs();
        return ResponseEntity.ok(logs);
    }
}