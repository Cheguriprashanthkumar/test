package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.CompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyDetailsRepository extends JpaRepository<CompanyDetails, Long> {

    /**
     * Finds the single active company profile.
     */
    Optional<CompanyDetails> findByActive(boolean active);

    /**
     * Sets all company profiles to inactive.
     * This is used to ensure only one profile can be active at a time.
     */
    @Modifying
    @Query("UPDATE CompanyDetails c SET c.active = false")
    void deactivateAll();
}