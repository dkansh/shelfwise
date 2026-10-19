package com.shelfwise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shelfwise.config.LibraryProperties;
import com.shelfwise.dto.LoanRequest;
import com.shelfwise.dto.LoanResponse;
import com.shelfwise.exception.BookNotAvailableException;
import com.shelfwise.exception.LoanLimitExceededException;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Book;
import com.shelfwise.model.Loan;
import com.shelfwise.model.LoanStatus;
import com.shelfwise.model.Member;
import com.shelfwise.model.MembershipType;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.LoanRepository;
import com.shelfwise.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationService notificationService;

    private LoanService loanService;

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository, bookRepository, memberRepository,
                notificationService, new LibraryProperties());

        member = new Member();
        member.setId(1L);
        member.setName("Alice");
        member.setEmail("alice@example.com");
        member.setActive(true);
        member.setMembershipType(MembershipType.STANDARD);

        book = new Book();
        book.setId(10L);
        book.setTitle("Clean Code");
        book.setTotalCopies(2);
        book.setAvailableCopies(2);
    }

    @Test
    void checkoutBook_success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.countByMemberIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse response = loanService.checkoutBook(new LoanRequest(1L, 10L));

        assertEquals("ACTIVE", response.status());
        assertEquals(LocalDate.now().plusDays(14), response.dueDate());
        assertEquals(1, book.getAvailableCopies());
        verify(notificationService).sendCheckoutConfirmation(eq("alice@example.com"), eq("Clean Code"), any(LocalDate.class));
    }

    @Test
    void checkoutBook_failsWhenLimitReached() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.countByMemberIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(5L);

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.checkoutBook(new LoanRequest(1L, 10L)));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void checkoutBook_failsWhenNoCopiesLeft() {
        book.setAvailableCopies(0);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.countByMemberIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(0L);

        assertThrows(BookNotAvailableException.class,
                () -> loanService.checkoutBook(new LoanRequest(1L, 10L)));
    }

    @Test
    void checkoutBook_failsWhenMemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanService.checkoutBook(new LoanRequest(99L, 10L)));
    }

    @Test
    void returnBook_marksReturnedAndPutsCopyBack() {
        book.setAvailableCopies(1);
        Loan loan = new Loan();
        loan.setId(5L);
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now().minusDays(7));
        loan.setDueDate(LocalDate.now().plusDays(7));
        loan.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse response = loanService.returnBook(5L);

        assertEquals("RETURNED", response.status());
        assertEquals(2, book.getAvailableCopies());
        assertEquals(0.0, response.fineAmount());
    }

    @Test
    void calculateFine_isZeroWhenReturnedOnTime() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.of(2024, 3, 10));
        loan.setReturnDate(LocalDate.of(2024, 3, 8));

        assertEquals(0.0, loanService.calculateFine(loan));
    }

    @Test
    void calculateFine_lateReturn() {
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.of(2024, 3, 1));
        loan.setReturnDate(LocalDate.of(2024, 3, 4));

        assertEquals(0.75, loanService.calculateFine(loan), 0.001);
    }

    @Test
    @Disabled("renewals not implemented yet")
    void renewLoan_extendsDueDate() {
        // TODO
    }
}
