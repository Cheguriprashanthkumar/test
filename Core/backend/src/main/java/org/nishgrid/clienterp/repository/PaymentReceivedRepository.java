package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.PaymentReceived;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentReceivedRepository extends JpaRepository<PaymentReceived, Long> {
}
