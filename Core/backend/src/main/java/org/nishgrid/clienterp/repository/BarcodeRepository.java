package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {
    List<Barcode> findByGrnId(Long grnId);
}