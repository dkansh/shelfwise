package com.shelfwise.dto;

public record PopularBookDto(Long bookId, String title, long timesBorrowed) {
}
