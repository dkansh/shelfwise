package com.shelfwise.event;

/**
 * Published by LoanService after a book is returned.
 */
public record BookReturnedEvent(Long loanId, Long bookId, Long memberId, double fine) {
}
