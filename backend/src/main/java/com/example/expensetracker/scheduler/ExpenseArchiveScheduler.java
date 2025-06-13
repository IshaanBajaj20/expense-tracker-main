package com.example.expensetracker.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.expensetracker.service.ExpenseService;

/**
 * Scheduler component that runs periodic background tasks related to expenses.
 * This class automatically archives expenses older than 30 days.
 */
@Component
public class ExpenseArchiveScheduler {

    @Autowired
    private ExpenseService expenseService;

    /**
     * Scheduled task that runs every day at 1 AM server time.
     * Archives all expenses that are older than 30 days and not already archived.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoArchiveOldExpenses() {
        expenseService.archiveOldExpenses();
    }
}
