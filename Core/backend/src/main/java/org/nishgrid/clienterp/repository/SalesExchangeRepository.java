package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.SalesExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesExchangeRepository extends JpaRepository<SalesExchange, Long> {
}