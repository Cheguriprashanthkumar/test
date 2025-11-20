package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.model.SalesAuditLog;
import org.nishgrid.clienterp.repository.SalesAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesAuditLogService {

    @Autowired
    private SalesAuditLogRepository auditLogRepository;

    public List<SalesAuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}