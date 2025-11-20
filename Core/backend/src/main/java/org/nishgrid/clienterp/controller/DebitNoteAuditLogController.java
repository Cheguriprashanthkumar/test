package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.DebitNoteAuditLogResponse;
import org.nishgrid.clienterp.repository.DebitNoteAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debit-note-audit-logs")
public class DebitNoteAuditLogController {

    @Autowired
    private DebitNoteAuditLogRepository auditLogRepository;

    @GetMapping
    public List<DebitNoteAuditLogResponse> getAuditLogs() {
        return auditLogRepository.findAllWithDetails().stream()
                .map(DebitNoteAuditLogResponse::fromEntity)
                .collect(Collectors.toList());
    }
}