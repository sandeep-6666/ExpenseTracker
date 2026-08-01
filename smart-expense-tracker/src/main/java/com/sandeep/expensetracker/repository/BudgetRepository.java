package com.sandeep.expensetracker.repository;

import com.sandeep.expensetracker.entity.Budget;
import com.sandeep.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Data access for Budget entities. */
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserAndMonthAndYear(User user, int month, int year);
}
