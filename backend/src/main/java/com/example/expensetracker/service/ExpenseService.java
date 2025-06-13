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

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Page<Expense> getFilteredExpenses(Specification<Expense> spec, Pageable pageable) {
        return expenseRepository.findAll(spec, pageable);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

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

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public void softDeleteExpense(Long id) {
        Expense expense = getExpenseById(id);
        expense.setDeleted(true);
        expenseRepository.save(expense);
    }

    public List<Expense> getArchivedExpenses() {
        return expenseRepository.findByDeletedTrue();
    }

    public void archiveOldExpenses() {
        LocalDate cutoff = LocalDate.now().minusDays(30);
        List<Expense> oldExpenses = expenseRepository.findByDeletedFalse().stream()
                .filter(exp -> exp.getDate().isBefore(cutoff))
                .collect(Collectors.toList());

        for (Expense exp : oldExpenses) {
            exp.setDeleted(true);
            exp.setArchivedDate(LocalDate.now());
        }
        expenseRepository.saveAll(oldExpenses);
    }

}