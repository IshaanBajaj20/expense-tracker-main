package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling business logic related to expenses.
 * Includes CRUD operations, filtering, soft deletes, and auto-archiving.
 */
@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    /**
     * Saves a new expense record.
     */
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    /**
     * Fetches all expense records (use cautiously).
     */
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    /**
     * Retrieves expenses matching the given filters and pagination settings.
     */
    public Page<Expense> getFilteredExpenses(Specification<Expense> spec, Pageable pageable) {
        return expenseRepository.findAll(spec, pageable);
    }

    /**
     * Finds a specific expense by ID.
     */
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    /**
     * Updates the fields of an existing expense.
     */
    public Expense updateExpense(Long id, Expense expense) {
        Expense existing = getExpenseById(id);
        if (existing != null) {
            existing.setDescription(expense.getDescription());
            existing.setAmount(expense.getAmount());
            existing.setDate(expense.getDate());
            return expenseRepository.save(existing);
        }
        return null;
    }

    /**
     * Hard deletes an expense (permanent).
     */
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    /**
     * Marks an expense as deleted (soft delete).
     */
    public void softDeleteExpense(Long id) {
        Expense expense = getExpenseById(id);
        if (expense != null) {
            expense.setDeleted(true);
            expenseRepository.save(expense);
        }
    }

    /**
     * Returns all expenses that are soft-deleted (archived).
     */
    public List<Expense> getArchivedExpenses() {
        return expenseRepository.findByDeletedTrue();
    }

    /**
     * Archives all expenses older than 30 days and not already archived.
     * This is triggered by a scheduled task.
     */
    public void archiveOldExpenses() {
        LocalDate cutoff = LocalDate.now().minusDays(30);
        List<Expense> oldExpenses = expenseRepository.findByDeletedFalse().stream()
                .filter(exp -> exp.getDate().isBefore(cutoff)).collect(Collectors.toList());

        for (Expense exp : oldExpenses) {
            exp.setDeleted(true);
            exp.setArchivedDate(LocalDate.now());
        }
        expenseRepository.saveAll(oldExpenses);
    }

}