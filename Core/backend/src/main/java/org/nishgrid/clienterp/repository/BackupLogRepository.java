package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.BackupLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupLogRepository extends JpaRepository<BackupLog, Long> {}