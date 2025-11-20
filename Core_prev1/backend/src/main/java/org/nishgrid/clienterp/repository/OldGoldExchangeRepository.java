package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.OldGoldExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OldGoldExchangeRepository extends JpaRepository<OldGoldExchange, Long> {

    Optional<OldGoldExchange> findFirstByOrderByPurchaseBillNoDesc();
}