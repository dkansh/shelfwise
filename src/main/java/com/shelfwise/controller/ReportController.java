package com.shelfwise.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.DashboardStats;
import com.shelfwise.dto.MonthlyLoanStat;
import com.shelfwise.dto.PopularBookDto;
import com.shelfwise.service.ReportService;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public DashboardStats dashboard() {
        return reportService.getDashboardStats();
    }

    @GetMapping("/popular-books")
    public List<PopularBookDto> popularBooks(@RequestParam(defaultValue = "10") int limit) {
        return reportService.getMostBorrowedBooks(limit);
    }

    @GetMapping("/monthly")
    public List<MonthlyLoanStat> monthly(@RequestParam(required = false) Integer year) {
        return reportService.getMonthlyLoanStats(year != null ? year : LocalDate.now().getYear());
    }

    @GetMapping("/member/{memberId}/activity")
    public Map<String, Object> memberActivity(@PathVariable Long memberId) {
        return reportService.getMemberActivity(memberId);
    }

    @GetMapping("/branches/inventory")
    public List<Map<String, Object>> branchInventory() {
        return reportService.getBranchInventory();
    }
}
