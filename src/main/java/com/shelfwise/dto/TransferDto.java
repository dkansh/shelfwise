package com.shelfwise.dto;

import java.time.LocalDateTime;

import com.shelfwise.model.TransferStatus;

public record TransferDto(
        Long id,
        String copyBarcode,
        String bookTitle,
        String fromBranch,
        String toBranch,
        TransferStatus status,
        String requestedBy,
        LocalDateTime requestedAt,
        LocalDateTime shippedAt,
        LocalDateTime receivedAt) {
}
