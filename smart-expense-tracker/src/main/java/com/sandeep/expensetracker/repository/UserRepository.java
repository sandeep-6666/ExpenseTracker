package com.sandeep.expensetracker.repository;

import com.sandeep.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Data access for User entities. */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
