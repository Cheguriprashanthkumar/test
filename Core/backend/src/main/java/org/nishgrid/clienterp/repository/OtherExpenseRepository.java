package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.OtherExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OtherExpenseRepository extends JpaRepository<OtherExpense, Long> {
    List<OtherExpense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
}