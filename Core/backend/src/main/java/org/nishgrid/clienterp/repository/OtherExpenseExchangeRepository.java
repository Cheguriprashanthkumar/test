package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.OtherExpenseExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OtherExpenseExchangeRepository extends JpaRepository<OtherExpenseExchange, Long> {

    @Query("SELECT oee FROM OtherExpenseExchange oee " +
            "JOIN FETCH oee.oldExpense " +
            "JOIN FETCH oee.newExpense " +
            "LEFT JOIN FETCH oee.vendor " +
            "ORDER BY oee.exchangeDate DESC")
    List<OtherExpenseExchange> findAllWithDetails();
}