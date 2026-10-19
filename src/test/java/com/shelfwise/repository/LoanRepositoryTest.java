package com.shelfwise.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.shelfwise.model.Book;
import com.shelfwise.model.Loan;
import com.shelfwise.model.LoanStatus;
import com.shelfwise.model.Member;
import com.shelfwise.model.MembershipType;

@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setName("Test Member");
        member.setEmail("test@example.com");
        member.setMembershipType(MembershipType.STANDARD);
        member.setActive(true);
        member.setJoinedDate(new Date());
        memberRepository.save(member);

        book = new Book();
        book.setIsbn("9780451524935");
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
        bookRepository.save(book);
    }

    @Test
    void countByMemberIdAndStatus_onlyCountsThatStatus() {
        loanRepository.save(loan(LoanStatus.ACTIVE, LocalDate.now().plusDays(5)));
        loanRepository.save(loan(LoanStatus.ACTIVE, LocalDate.now().minusDays(2)));
        loanRepository.save(loan(LoanStatus.RETURNED, LocalDate.now().minusDays(20)));

        assertEquals(2, loanRepository.countByMemberIdAndStatus(member.getId(), LoanStatus.ACTIVE));
        assertEquals(1, loanRepository.countByMemberIdAndStatus(member.getId(), LoanStatus.RETURNED));
    }

    @Test
    void findByDueDateBeforeAndStatus_findsLateLoans() {
        loanRepository.save(loan(LoanStatus.ACTIVE, LocalDate.now().plusDays(5)));
        loanRepository.save(loan(LoanStatus.ACTIVE, LocalDate.now().minusDays(2)));

        List<Loan> late = loanRepository.findByDueDateBeforeAndStatus(LocalDate.now(), LoanStatus.ACTIVE);

        assertEquals(1, late.size());
    }

    private Loan loan(LoanStatus status, LocalDate dueDate) {
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(dueDate.minusDays(14));
        loan.setDueDate(dueDate);
        loan.setStatus(status);
        return loan;
    }
}
