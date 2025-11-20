package org.nishgrid.clienterp.controller;

import org.nishgrid.clienterp.model.BackupLog;
import org.nishgrid.clienterp.repository.BackupLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/backup-logs")
public class BackupLogController {

    @Autowired
    private BackupLogRepository backupLogRepository;

    @GetMapping
    public List<BackupLog> getAllLogs() {

        return backupLogRepository.findAll(Sort.by(Sort.Direction.DESC, "performedAt"));
    }
}