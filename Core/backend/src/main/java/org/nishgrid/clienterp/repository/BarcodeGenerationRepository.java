package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.BarcodeGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarcodeGenerationRepository extends JpaRepository<BarcodeGeneration, Long> {
}