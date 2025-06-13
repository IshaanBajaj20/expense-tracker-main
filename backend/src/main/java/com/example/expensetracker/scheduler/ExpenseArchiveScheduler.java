package com.example.expensetracker.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.expensetracker.service.ExpenseService;

@Component
public class ExpenseArchiveScheduler {

    @Autowired
    private ExpenseService expenseService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void autoArchiveOldExpenses() {
        expenseService.archiveOldExpenses();
    }
}
