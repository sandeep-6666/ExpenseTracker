package com.sandeep.expensetracker.repository;

import com.sandeep.expensetracker.entity.Category;
import com.sandeep.expensetracker.entity.Expense;
import com.sandeep.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Data access for Expense entities, with filtering/search query methods. */
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserOrderByExpenseDateDesc(User user);

    List<Expense> findByUserAndCategory(User user, Category category);

    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate start, LocalDate end);

    List<Expense> findByUserAndAmountBetween(User user, BigDecimal min, BigDecimal max);

    @Query("SELECT e FROM Expense e WHERE e.user = :user " +
           "AND (:keyword IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Expense> searchByDescription(@Param("user") User user, @Param("keyword") String keyword);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user " +
           "AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    BigDecimal sumByUserAndMonth(@Param("user") User user, @Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user")
    BigDecimal sumByUser(@Param("user") User user);

    @Query("SELECT e.category, COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user GROUP BY e.category")
    List<Object[]> sumByCategoryForUser(@Param("user") User user);

    @Query("SELECT e.category, COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user " +
           "AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year GROUP BY e.category")
    List<Object[]> sumByCategoryForUserAndMonth(@Param("user") User user, @Param("month") int month, @Param("year") int year);
}
