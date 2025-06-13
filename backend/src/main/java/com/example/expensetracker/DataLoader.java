package com.example.expensetracker;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public void run(String... args) {
        if (expenseRepository.count() == 0) {
            expenseRepository.save(new Expense("Groceries", 45.99, LocalDate.of(2025, 6, 1), "Food"));
            expenseRepository.save(new Expense("Netflix", 12.99, LocalDate.of(2025, 6, 3), "Entertainment"));
            expenseRepository.save(new Expense("Uber", 18.00, LocalDate.of(2025, 5, 29), "Transport"));
            expenseRepository.save(new Expense("Flight", 320.00, LocalDate.of(2025, 6, 5), "Travel"));
        }
    }
}
