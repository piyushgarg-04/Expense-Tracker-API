package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find expenses by user's email
    List<Expense> findByUserEmail(String email);

    // Find expense by id and user's email
    Optional<Expense> findByIdAndUserEmail(Long id, String email);

    // Find expenses by user email and date range
    List<Expense> findByUserEmailAndDateBetween(String email, LocalDate start, LocalDate end);
}
