package com.shelfwise.fines;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FineDto(
        Long id,
        Long loanId,
        Long memberId,
        BigDecimal amount,
        BigDecimal paidAmount,
        BigDecimal remaining,
        FineStatus status,
        String reason,
        LocalDateTime createdAt) {
}
