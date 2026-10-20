package com.shelfwise.events;

import java.time.LocalDateTime;

public record LibraryEventDto(
        Long id,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String branchName,
        int capacity,
        long registered,
        EventStatus status) {
}
