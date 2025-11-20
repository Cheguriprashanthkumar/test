package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.OtherExpenseReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OtherExpenseReturnRepository extends JpaRepository<OtherExpenseReturn, Long> {
    @Query("SELECT oer FROM OtherExpenseReturn oer JOIN FETCH oer.otherExpense oe ORDER BY oer.returnDate DESC")
    List<OtherExpenseReturn> findAllWithDetails();
}