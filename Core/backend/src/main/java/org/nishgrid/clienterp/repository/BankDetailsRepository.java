package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
    List<BankDetails> findByStatus(BankDetails.Status status);

}