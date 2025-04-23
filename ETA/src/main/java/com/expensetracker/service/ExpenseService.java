package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public Expense addExpense(ExpenseRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .category(request.getCategory())
                .date(request.getDate() == null ? LocalDate.now() : request.getDate())
                .user(user)
                .build();
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses(String email) {
        return expenseRepository.findByUserEmail(email);
    }

    public Expense updateExpense(Long id, ExpenseRequest request, String email) {
        Expense expense = expenseRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));

        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id, String email) {
        Expense expense = expenseRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        expenseRepository.delete(expense);
    }

    public double getTotalExpense(String email, LocalDate start, LocalDate end) {
        return expenseRepository.findByUserEmailAndDateBetween(email, start, end)
                .stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getExpensesByCategory(String email) {
        List<Expense> expenses = expenseRepository.findByUserEmail(email);
        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));
    }

    public Map<String, Object> getMonthlyReport(String email, int month, int year) {
        List<Expense> expenses = expenseRepository.findByUserEmail(email);

        List<Expense> monthlyExpenses = expenses.stream()
                .filter(e -> e.getDate().getMonthValue() == month && e.getDate().getYear() == year)
                .toList();

        double total = monthlyExpenses.stream().mapToDouble(Expense::getAmount).sum();
        Map<String, Double> byCategory = monthlyExpenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

        Map<String, Object> report = new HashMap<>();
        report.put("total", total);
        report.put("breakdown", byCategory);

        return report;
    }
}