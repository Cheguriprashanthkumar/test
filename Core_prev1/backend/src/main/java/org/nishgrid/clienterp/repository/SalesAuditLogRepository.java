package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.SalesAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesAuditLogRepository extends JpaRepository<SalesAuditLog, Long> {


    List<SalesAuditLog> findAllByOrderByTimestampDesc();
}