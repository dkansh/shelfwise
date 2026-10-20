package com.shelfwise.dto;

import jakarta.validation.constraints.NotNull;

public record TransferRequest(@NotNull Long copyId, @NotNull Long toBranchId) {
}
