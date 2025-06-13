package com.example.expensetracker.repository;

import com.example.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing Expense entities.
 * Extends JpaRepository for CRUD and JpaSpecificationExecutor for dynamic filtering.
 */
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    /**
     * Finds all active (non-deleted) expenses.
     *
     * @return list of active expenses
     */
    List<Expense> findByDeletedFalse();

    /**
     * Finds all soft-deleted expenses.
     *
     * @return list of deleted expenses
     */
    List<Expense> findByDeletedTrue();

    /**
     * Finds all non-archived expenses with a date older than the given date.
     * Used for identifying expenses that need to be auto-archived.
     *
     * @param date the cutoff date
     * @return list of old, unarchived expenses
     */
    List<Expense> findByDateBeforeAndArchivedFalse(LocalDate date);

}