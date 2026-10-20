package com.shelfwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePurchaseRequest(
        @NotBlank String title,
        String author,
        String isbn,
        @NotNull Long memberId,
        String reason) {
}
