package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.LicenseDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LicenseDetailsRepository extends JpaRepository<LicenseDetails, Long> {
    Optional<LicenseDetails> findByLicenseKey(String licenseKey);
    Optional<LicenseDetails> findTopByClientIdIsNullOrderByIdDesc();

    Optional<LicenseDetails> findTopByClientIsNullOrderByIdDesc();

    Optional<LicenseDetails> findTopByOrderByEndDateDesc();

    List<LicenseDetails> findAllByLicenseKey(String licenseKey);
}
