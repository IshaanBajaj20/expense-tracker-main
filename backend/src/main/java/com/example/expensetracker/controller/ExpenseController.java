package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.specification.ExpenseSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFilteredExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Expense> spec = ExpenseSpecification.withFilters(
                category, startDate, endDate, minAmount, maxAmount
        );

        Page<Expense> expensePage = expenseService.getFilteredExpenses(spec, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("expenses", expensePage.getContent());
        response.put("totalItems", expensePage.getTotalElements());
        response.put("totalPages", expensePage.getTotalPages());
        response.put("currentPage", expensePage.getNumber());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @PostMapping
    public Expense createExpense(@Valid @RequestBody Expense expense) {
        return expenseService.createExpense(expense);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        return expenseService.updateExpense(id, expense);
    }

    @DeleteMapping("/archive/{id}")
    public ResponseEntity<Void> archiveExpense(@PathVariable Long id) {
        expenseService.softDeleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/archived")
    public ResponseEntity<Map<String, Object>> getArchivedExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Expense> archivedList = expenseService.getArchivedExpenses();

        Map<String, Object> response = new HashMap<>();
        response.put("expenses", archivedList);
        response.put("totalItems", archivedList.size());
        response.put("totalPages", 1);
        response.put("currentPage", 0);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }
}