package com.shelfwise.event;

/**
 * Published by LoanService after a book is checked out.
 */
public record LoanCreatedEvent(Long loanId, Long memberId, Long bookId) {
}
