package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
}