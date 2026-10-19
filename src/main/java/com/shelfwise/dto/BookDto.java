package com.shelfwise.dto;

public record BookDto(
        Long id,
        String isbn,
        String title,
        String author,
        Integer publishedYear,
        String genre,
        int totalCopies,
        int availableCopies) {
}
