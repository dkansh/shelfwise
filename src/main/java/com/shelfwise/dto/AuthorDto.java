package com.shelfwise.dto;

public record AuthorDto(
        Long id,
        String name,
        String bio,
        Integer birthYear,
        String nationality,
        int bookCount) {
}
