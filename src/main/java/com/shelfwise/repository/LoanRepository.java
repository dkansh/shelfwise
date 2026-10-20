package com.shelfwise.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shelfwise.model.Loan;
import com.shelfwise.model.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    long countByMemberIdAndStatus(Long memberId, LoanStatus status);

    long countByStatus(LoanStatus status);

    List<Loan> findByMemberId(Long memberId);

    List<Loan> findByBookId(Long bookId);

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByBookIdAndStatus(Long bookId, LoanStatus status);

    List<Loan> findByDueDateBeforeAndStatus(LocalDate date, LoanStatus status);

    List<Loan> findByLoanDateBetween(LocalDate from, LocalDate to);

    // returns [bookId, title, count]
    @Query("select l.book.id, l.book.title, count(l) from Loan l "
            + "group by l.book.id, l.book.title order by count(l) desc")
    List<Object[]> findMostBorrowedBooks(Pageable pageable);
}
