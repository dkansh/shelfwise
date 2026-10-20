package com.shelfwise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.shelfwise.config.LibraryProperties;
import com.shelfwise.dto.LoanRequest;
import com.shelfwise.dto.LoanResponse;
import com.shelfwise.event.BookReturnedEvent;
import com.shelfwise.event.LoanCreatedEvent;
import com.shelfwise.exception.BookNotAvailableException;
import com.shelfwise.exception.LoanLimitExceededException;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.fines.FineService;
import com.shelfwise.model.Book;
import com.shelfwise.model.Loan;
import com.shelfwise.model.LoanStatus;
import com.shelfwise.model.Member;
import com.shelfwise.model.MembershipType;
import com.shelfwise.model.Reservation;
import com.shelfwise.repository.BookCopyRepository;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.LoanRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private FineService fineService;

    @Mock
    private LibraryCalendarService calendarService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private LoanService loanService;

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository, bookRepository, memberRepository, bookCopyRepository,
                reservationRepository, notificationService, fineService, calendarService, eventPublisher,
                new LibraryProperties());

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
        when(fineService.getOutstandingBalance(1L)).thenReturn(BigDecimal.ZERO);
        when(loanRepository.countByMemberIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse response = loanService.checkoutBook(new LoanRequest(1L, 10L));

        assertEquals("ACTIVE", response.status());
        assertEquals(LocalDate.now().plusDays(14), response.dueDate());
        assertEquals(1, book.getAvailableCopies());
        verify(notificationService).sendCheckoutConfirmation(eq("alice@example.com"), eq("Clean Code"), any(LocalDate.class));
        verify(eventPublisher).publishEvent(any(LoanCreatedEvent.class));
    }

    @Test
    void checkoutBook_failsWhenMemberOwesTooMuch() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(fineService.getOutstandingBalance(1L)).thenReturn(new BigDecimal("12.50"));

        assertThrows(IllegalStateException.class, () -> loanService.checkoutBook(new LoanRequest(1L, 10L)));
    }

    @Test
    void checkoutBook_failsWhenLimitReached() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(fineService.getOutstandingBalance(1L)).thenReturn(BigDecimal.ZERO);
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
        when(fineService.getOutstandingBalance(1L)).thenReturn(BigDecimal.ZERO);
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
    @Disabled("TODO: premium members should get 10 loans, not implemented yet")
    void checkoutBook_premiumMemberCanHaveMoreLoans() {
    }

    @Test
    void returnBook_marksReturnedAndPutsCopyBack() {
        book.setAvailableCopies(1);
        Loan loan = activeLoan(LocalDate.now().plusDays(7));

        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse response = loanService.returnBook(5L);

        assertEquals("RETURNED", response.status());
        assertEquals(2, book.getAvailableCopies());
        assertEquals(0.0, response.fineAmount());
        verify(fineService, never()).createFineForLoan(any(), anyDouble());
        verify(eventPublisher).publishEvent(any(BookReturnedEvent.class));
    }

    @Test
    void returnBook_lateReturnCreatesFine() {
        Loan loan = activeLoan(LocalDate.now().minusDays(4));

        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse response = loanService.returnBook(5L);

        assertEquals(1.0, response.fineAmount(), 0.001);
        verify(fineService).createFineForLoan(loan, 1.0);
    }

    @Test
    void renewLoan_extendsDueDate() {
        LocalDate due = LocalDate.of(2026, 3, 2);
        Loan loan = activeLoan(due);

        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(calendarService.nextOpenDay(any(LocalDate.class))).thenAnswer(inv -> inv.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse response = loanService.renewLoan(5L);

        assertEquals(due.plusDays(14), response.dueDate());
        assertEquals(1, loan.getRenewalCount());
    }

    @Test
    void renewLoan_notAllowedWhenSomeoneIsWaiting() {
        Loan loan = activeLoan(LocalDate.now().plusDays(3));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(reservationRepository.findByBookIdAndStatusOrderByReservedAtAsc(10L, "PENDING"))
                .thenReturn(List.of(new Reservation()));

        assertThrows(IllegalStateException.class, () -> loanService.renewLoan(5L));
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

    private Loan activeLoan(LocalDate dueDate) {
        Loan loan = new Loan();
        loan.setId(5L);
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(dueDate.minusDays(14));
        loan.setDueDate(dueDate);
        loan.setStatus(LoanStatus.ACTIVE);
        return loan;
    }
}
