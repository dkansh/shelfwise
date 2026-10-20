package com.shelfwise.dto;

import java.util.List;

/**
 * Bigger version of BookDto used by the v2 endpoints.
 */
public record BookDetailDto(
        Long id,
        String isbn,
        String title,
        String author,
        Long authorId,
        Integer publishedYear,
        String genre,
        String language,
        Integer pageCount,
        String description,
        List<String> categories,
        int totalCopies,
        int availableCopies,
        Double averageRating) {
}
