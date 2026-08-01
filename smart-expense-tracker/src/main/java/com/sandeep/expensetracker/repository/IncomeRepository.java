package com.sandeep.expensetracker.repository;

import com.sandeep.expensetracker.entity.Income;
import com.sandeep.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/** Data access for Income entities. */
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserOrderByIncomeDateDesc(User user);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user = :user " +
           "AND MONTH(i.incomeDate) = :month AND YEAR(i.incomeDate) = :year")
    BigDecimal sumByUserAndMonth(@Param("user") User user, @Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user = :user")
    BigDecimal sumByUser(@Param("user") User user);
}
