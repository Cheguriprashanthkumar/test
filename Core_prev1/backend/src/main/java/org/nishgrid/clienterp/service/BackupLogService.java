package org.nishgrid.clienterp.service;

import org.nishgrid.clienterp.model.BackupLog;
import org.nishgrid.clienterp.repository.BackupLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BackupLogService {

    @Autowired
    private BackupLogRepository backupLogRepository;

    public void createLog(BackupLog.ActionType actionType,
                          String fileName,
                          Long fileSize,
                          String performedBy,
                          BackupLog.Status status,
                          String remarks) {

        BackupLog log = new BackupLog();
        log.setActionType(actionType);
        log.setFileName(fileName);
        log.setFileSize(fileSize);
        log.setPerformedBy(performedBy);
        log.setStatus(status);
        log.setRemarks(remarks);

        backupLogRepository.save(log);
    }
}