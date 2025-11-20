package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.CustomerKyc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerKycRepository extends JpaRepository<CustomerKyc, Long> {
    Optional<CustomerKyc> findByCustomer_CustomerId(Long customerId);
    void deleteByCustomer_CustomerId(Long customerId);
}