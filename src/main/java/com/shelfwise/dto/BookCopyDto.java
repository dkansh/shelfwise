package com.shelfwise.dto;

import java.time.LocalDate;

import com.shelfwise.model.CopyCondition;
import com.shelfwise.model.CopyStatus;

public record BookCopyDto(
        Long id,
        String barcode,
        Long bookId,
        String bookTitle,
        String branchCode,
        CopyCondition condition,
        CopyStatus status,
        LocalDate acquiredDate) {
}
