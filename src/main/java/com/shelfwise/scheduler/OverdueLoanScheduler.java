package com.shelfwise.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shelfwise.model.Loan;
import com.shelfwise.service.LoanService;
import com.shelfwise.service.NotificationService;

/**
 * Nightly job that flags late loans and reminds members to bring the books back.
 */
@Component
public class OverdueLoanScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueLoanScheduler.class);

    private final LoanService loanService;
    private final NotificationService notificationService;

    public OverdueLoanScheduler(LoanService loanService, NotificationService notificationService) {
        this.loanService = loanService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${shelfwise.overdue-cron}")
    public void checkOverdueLoans() {
        log.info("Running overdue loan check...");

        List<Loan> overdue = loanService.markOverdueLoans();
        for (Loan loan : overdue) {
            notificationService.sendOverdueReminder(
                    loan.getMember().getEmail(),
                    loan.getBook().getTitle(),
                    loanService.calculateFine(loan));
        }

        log.info("Marked {} loans as overdue", overdue.size());
    }
}
