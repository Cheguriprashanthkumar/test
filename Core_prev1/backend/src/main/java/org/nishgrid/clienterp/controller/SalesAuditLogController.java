package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.dto.SalesAuditLogDTO;
import org.nishgrid.clienterp.model.SalesAuditLog;
import org.nishgrid.clienterp.service.SalesAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit-logs")
public class SalesAuditLogController {

    @Autowired
    private SalesAuditLogService auditLogService;

    @GetMapping
    public List<SalesAuditLogDTO> getAllAuditLogs() {
        return auditLogService.getAllLogs().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private SalesAuditLogDTO convertToDto(SalesAuditLog log) {
        SalesAuditLogDTO dto = new SalesAuditLogDTO();
        dto.setLogId(log.getLogId());
        dto.setAction(log.getAction());
        dto.setPerformedBy(log.getPerformedBy());
        dto.setTimestamp(log.getTimestamp());
        dto.setOldData(log.getOldData());
        dto.setNewData(log.getNewData());
        if (log.getSalesInvoice() != null) {
            dto.setInvoiceId(log.getSalesInvoice().getInvoiceId());
        }
        return dto;
    }

}