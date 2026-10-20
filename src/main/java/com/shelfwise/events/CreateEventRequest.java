package com.shelfwise.events;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEventRequest(
        @NotBlank String title,
        String description,
        @NotNull @Future LocalDateTime startTime,
        LocalDateTime endTime,
        @NotNull Long branchId,
        @Min(1) int capacity) {
}
