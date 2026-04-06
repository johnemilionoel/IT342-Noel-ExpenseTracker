package edu.cit.noel.expensetracker.repository;

import edu.cit.noel.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    java.math.BigDecimal getTotalByUserId(Long userId);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.user.id = :userId")
    Long getCountByUserId(Long userId);

    @Query("SELECT e.category.name, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId GROUP BY e.category.name")
    List<Object[]> getSummaryByCategory(Long userId);
}
