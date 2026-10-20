package com.shelfwise.dto;

import java.time.LocalDateTime;

public record ReviewDto(
        Long id,
        Long bookId,
        Long memberId,
        String memberName,
        int rating,
        String comment,
        LocalDateTime createdAt) {
}
