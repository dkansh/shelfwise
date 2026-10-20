package com.shelfwise.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DashboardStats {

    private long totalBooks;
    private int totalMembers;
    private long activeMembers;
    private long activeLoans;
    private long overdueLoans;
    private long pendingReservations;
    private long cancelledReservations;
    private BigDecimal outstandingFines;
    private LocalDateTime generatedAt;
}
