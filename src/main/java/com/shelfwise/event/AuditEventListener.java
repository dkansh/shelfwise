package com.shelfwise.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.shelfwise.service.AuditService;

/**
 * Writes loan activity to the audit log once the transaction is committed
 * (so we don't log stuff that got rolled back).
 */
@Component
public class AuditEventListener {

    private final AuditService auditService;

    public AuditEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @TransactionalEventListener
    public void onLoanCreated(LoanCreatedEvent event) {
        auditService.log("CHECKOUT", "Loan", event.loanId(),
                "member=" + event.memberId() + ", book=" + event.bookId());
    }

    @TransactionalEventListener
    public void onBookReturned(BookReturnedEvent event) {
        auditService.log("RETURN", "Loan", event.loanId(), "fine=" + event.fine());
    }
}
