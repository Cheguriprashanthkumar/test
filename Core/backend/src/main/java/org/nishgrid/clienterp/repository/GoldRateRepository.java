package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.GoldRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoldRateRepository extends JpaRepository<GoldRate, Long> {


    GoldRate findTopByOrderByDateDesc();
}