package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExpenseSuccess() {
        Expense expense = new Expense("Lunch", 15.0, LocalDate.now(), "Food");
        when(expenseRepository.save(expense)).thenReturn(expense);

        Expense saved = expenseService.createExpense(expense);
        assertEquals("Lunch", saved.getDescription());
        assertEquals(15.0, saved.getAmount());
        assertEquals("Food", saved.getCategory());
    }

    @Test
    void testGetExpenseById() {
        Expense expense = new Expense("Travel", 200.0, LocalDate.now(), "Travel");
        expense.setId(1L);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        Expense found = expenseService.getExpenseById(1L);
        assertEquals("Travel", found.getDescription());
    }

    // --- Invalid Input Scenarios ---

    @Test
    void testCreateExpenseWithEmptyDescription() {
        Expense expense = new Expense("", 10.0, LocalDate.now(), "Food");

        assertThrows(IllegalArgumentException.class, () -> {
            validateExpense(expense);
            expenseService.createExpense(expense);
        });
    }

    @Test
    void testCreateExpenseWithNegativeAmount() {
        Expense expense = new Expense("Coffee", -5.0, LocalDate.now(), "Food");

        assertThrows(IllegalArgumentException.class, () -> {
            validateExpense(expense);
            expenseService.createExpense(expense);
        });
    }

    @Test
    void testCreateExpenseWithZeroAmount() {
        Expense expense = new Expense("Water", 0.0, LocalDate.now(), "Food");

        assertThrows(IllegalArgumentException.class, () -> {
            validateExpense(expense);
            expenseService.createExpense(expense);
        });
    }

    @Test
    void testCreateExpenseWithFutureDate() {
        Expense expense = new Expense("Gym", 50.0, LocalDate.now().plusDays(1), "Health");

        assertThrows(IllegalArgumentException.class, () -> {
            validateExpense(expense);
            expenseService.createExpense(expense);
        });
    }

    @Test
    void testCreateExpenseWithEmptyCategory() {
        Expense expense = new Expense("Bus", 5.0, LocalDate.now(), "");

        assertThrows(IllegalArgumentException.class, () -> {
            validateExpense(expense);
            expenseService.createExpense(expense);
        });
    }

    private void validateExpense(Expense expense) {
        if (expense.getDescription() == null || expense.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (expense.getAmount() == null || expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (expense.getDate() == null || expense.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
    }
}
