package com.shelfwise.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shelfwise.model.Loan;
import com.shelfwise.model.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    long countByMemberIdAndStatus(Long memberId, LoanStatus status);

    List<Loan> findByMemberId(Long memberId);

    List<Loan> findByBookIdAndStatus(Long bookId, LoanStatus status);

    List<Loan> findByDueDateBeforeAndStatus(LocalDate date, LoanStatus status);
}
