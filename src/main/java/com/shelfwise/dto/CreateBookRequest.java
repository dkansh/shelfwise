package com.shelfwise.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating (and updating) a book.
 */
public record CreateBookRequest(
        @NotBlank String isbn,
        @NotBlank(message = "title is required") String title,
        @NotBlank String author,
        Integer publishedYear,
        String genre,
        @Min(value = 1, message = "must have at least one copy") int totalCopies) {
}
