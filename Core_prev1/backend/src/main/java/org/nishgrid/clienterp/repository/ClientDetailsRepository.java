package org.nishgrid.clienterp.repository;


import org.nishgrid.clienterp.model.ClientDetails;
import org.nishgrid.clienterp.model.LicenseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientDetailsRepository extends JpaRepository<ClientDetails, Integer> {

    Optional<ClientDetails> findById(Integer id);
    Optional<ClientDetails> findByEmailAddressIgnoreCase(String emailAddress);
    Optional<ClientDetails> findByEmailAddress(String emailAddress);

}