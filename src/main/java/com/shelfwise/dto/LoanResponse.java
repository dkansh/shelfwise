package com.shelfwise.dto;

import java.time.LocalDate;

public record LoanResponse(
        Long loanId,
        Long bookId,
        String bookTitle,
        Long memberId,
        String memberName,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate,
        String status,
        double fineAmount) {
}
