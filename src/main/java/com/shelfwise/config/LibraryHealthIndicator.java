package com.shelfwise.config;

import java.time.LocalDate;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import com.shelfwise.model.LoanStatus;
import com.shelfwise.repository.LoanRepository;

/**
 * Shows up in /actuator/health. Goes DOWN when there are too many overdue loans
 * so the librarians notice.
 */
@Component
public class LibraryHealthIndicator implements HealthIndicator {

    private static final int MAX_OVERDUE = 50;

    private final LoanRepository loanRepository;

    public LibraryHealthIndicator(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Health health() {
        int overdue = loanRepository.findByDueDateBeforeAndStatus(LocalDate.now(), LoanStatus.ACTIVE).size();
        if (overdue > MAX_OVERDUE) {
            return Health.down()
                    .withDetail("overdueLoans", overdue)
                    .withDetail("reason", "Too many overdue loans")
                    .build();
        }
        return Health.up().withDetail("overdueLoans", overdue).build();
    }
}
