package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.LicenseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<LicenseResponse, String> {

    Optional<LicenseResponse> findByLicenseKey(String licenseKey);
}
