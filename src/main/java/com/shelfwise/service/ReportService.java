package com.shelfwise.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.shelfwise.dto.DashboardStats;
import com.shelfwise.dto.MonthlyLoanStat;
import com.shelfwise.dto.PopularBookDto;
import com.shelfwise.fines.Fine;
import com.shelfwise.fines.FineRepository;
import com.shelfwise.fines.FineService;
import com.shelfwise.fines.FineStatus;
import com.shelfwise.model.BookCopy;
import com.shelfwise.model.LibraryBranch;
import com.shelfwise.model.Loan;
import com.shelfwise.model.LoanStatus;
import com.shelfwise.repository.BookCopyRepository;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.LibraryBranchRepository;
import com.shelfwise.repository.LoanRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.repository.ReservationRepository;

@Service
public class ReportService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private FineService fineService;

    @Autowired
    private LibraryBranchRepository branchRepository;

    @Autowired
    private BookCopyRepository copyRepository;

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalBooks(bookRepository.count());
        stats.setTotalMembers(memberRepository.findAll().size());
        stats.setActiveMembers(memberRepository.countByActiveTrue());
        stats.setActiveLoans(loanRepository.countByStatus(LoanStatus.ACTIVE));
        stats.setOverdueLoans(loanRepository.countByStatus(LoanStatus.OVERDUE));
        stats.setPendingReservations(reservationRepository.countByStatus("PENDING"));
        stats.setCancelledReservations(reservationRepository.countByStatus("CANCELLED"));

        BigDecimal outstanding = fineRepository.findAll().stream()
                .filter(f -> f.getStatus() == FineStatus.UNPAID)
                .map(Fine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setOutstandingFines(outstanding);
        stats.setGeneratedAt(LocalDateTime.now());
        return stats;
    }

    public List<PopularBookDto> getMostBorrowedBooks(int limit) {
        return loanRepository.findMostBorrowedBooks(PageRequest.of(0, limit)).stream()
                .map(row -> new PopularBookDto((Long) row[0], (String) row[1], (Long) row[2]))
                .toList();
    }

    public List<MonthlyLoanStat> getMonthlyLoanStats(int year) {
        Map<Integer, Long> counts = new TreeMap<>();
        for (Loan loan : loanRepository.findAll()) {
            if (loan.getLoanDate().getYear() == year) {
                counts.merge(loan.getLoanDate().getMonthValue(), 1L, Long::sum);
            }
        }

        List<MonthlyLoanStat> result = new ArrayList<>();
        counts.forEach((month, count) -> result.add(new MonthlyLoanStat(Month.of(month).name(), count)));
        return result;
    }

    public Map<String, Object> getMemberActivity(Long memberId) {
        List<Loan> loans = loanRepository.findByMemberId(memberId);

        String favouriteGenre = loans.stream()
                .map(l -> l.getBook().getGenre())
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Map<String, Object> result = new HashMap<>();
        result.put("memberId", memberId);
        result.put("totalLoans", loans.size());
        result.put("activeLoans", loans.stream().filter(l -> l.getStatus() == LoanStatus.ACTIVE).count());
        result.put("favouriteGenre", favouriteGenre);
        result.put("outstandingFines", fineService.getOutstandingBalance(memberId));
        return result;
    }

    public List<Map<String, Object>> getBranchInventory() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (LibraryBranch branch : branchRepository.findAll()) {
            List<BookCopy> copies = copyRepository.findByBranchId(branch.getId());
            Map<String, Object> row = new HashMap<>();
            row.put("branch", branch.getCode());
            row.put("totalCopies", copies.size());
            row.put("byStatus", copies.stream()
                    .collect(Collectors.groupingBy(c -> c.getStatus().name(), Collectors.counting())));
            result.add(row);
        }
        return result;
    }
}
