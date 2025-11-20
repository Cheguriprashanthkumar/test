package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.SmsNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsNotificationRepository extends JpaRepository<SmsNotification, Long> {
}