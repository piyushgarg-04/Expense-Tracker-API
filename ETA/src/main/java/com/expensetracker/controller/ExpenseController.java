package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody ExpenseRequest request, Principal principal) {
        return ResponseEntity.ok(expenseService.addExpense(request, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(Principal principal) {
        return ResponseEntity.ok(expenseService.getAllExpenses(principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody ExpenseRequest request, Principal principal) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id, Principal principal) {
        expenseService.deleteExpense(id, principal.getName());
        return ResponseEntity.ok("Expense deleted successfully");
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalExpense(
            Principal principal,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(expenseService.getTotalExpense(principal.getName(), start, end));
    }

    @GetMapping("/by-category")
    public ResponseEntity<Map<String, Double>> getExpensesByCategory(Principal principal) {
        return ResponseEntity.ok(expenseService.getExpensesByCategory(principal.getName()));
    }

    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(Principal principal,
        @RequestParam("month") int month,
        @RequestParam("year") int year) {
        return ResponseEntity.ok(expenseService.getMonthlyReport(principal.getName(), month, year));
    }
}