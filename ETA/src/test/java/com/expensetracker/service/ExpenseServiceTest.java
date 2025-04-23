package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("test@example.com").name("Test User").active(true).build();
    }

    @Test
    public void testAddExpense() {
        ExpenseRequest request = new ExpenseRequest(100.0, "Lunch", "Food", LocalDate.now());
        Expense expectedExpense = Expense.builder()
                .amount(100.0)
                .description("Lunch")
                .category("Food")
                .date(LocalDate.now())
                .user(user)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expectedExpense);

        Expense actualExpense = expenseService.addExpense(request, "test@example.com");

        assertEquals(expectedExpense.getAmount(), actualExpense.getAmount());
        assertEquals(expectedExpense.getCategory(), actualExpense.getCategory());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    public void testGetAllExpenses() {
        List<Expense> expenseList = List.of(
                Expense.builder().amount(50.0).category("Food").user(user).build(),
                Expense.builder().amount(30.0).category("Travel").user(user).build()
        );
        when(expenseRepository.findByUserEmail("test@example.com")).thenReturn(expenseList);

        List<Expense> result = expenseService.getAllExpenses("test@example.com");
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByUserEmail("test@example.com");
    }

    @Test
    public void testGetTotalExpense() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();
        List<Expense> expenses = List.of(
                Expense.builder().amount(100.0).user(user).date(LocalDate.now().minusDays(2)).build(),
                Expense.builder().amount(200.0).user(user).date(LocalDate.now()).build()
        );
        when(expenseRepository.findByUserEmailAndDateBetween("test@example.com", start, end)).thenReturn(expenses);

        double total = expenseService.getTotalExpense("test@example.com", start, end);
        assertEquals(300.0, total);
    }
}