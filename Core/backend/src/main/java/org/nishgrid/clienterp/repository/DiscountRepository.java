package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}