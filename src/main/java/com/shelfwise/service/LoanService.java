package com.shelfwise.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.LoanRepository;
import com.shelfwise.repository.MemberRepository;

@Service
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    // TODO: move these into LibraryProperties
    private static final int MAX_LOANS = 5;
    private static final double FINE_PER_DAY = 0.25;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final LibraryProperties libraryProperties;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       MemberRepository memberRepository,
                       NotificationService notificationService,
                       LibraryProperties libraryProperties) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
        this.libraryProperties = libraryProperties;
    }

    /**
     * Checks out a book for a member.
     *
     * @param request the member id and book id
     * @return the new loan
     * @throws LoanLimitExceededException if the member already has too many books
     *                                    (5 for standard members, 10 for premium)
     * @throws BookNotAvailableException  if there are no copies left
     */
    @Transactional
    public LoanResponse checkoutBook(LoanRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + request.getMemberId()));
        Book book = bookRepository.findById(request.getBookId()).get();

        if (!member.isActive()) {
            throw new IllegalStateException("Member " + member.getId() + " is not active");
        }

        long activeLoans = loanRepository.countByMemberIdAndStatus(member.getId(), LoanStatus.ACTIVE);
        if (activeLoans >= MAX_LOANS) {
            throw new LoanLimitExceededException(
                    "Member " + member.getId() + " already has " + activeLoans + " books checked out");
        }

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies of '" + book.getTitle() + "' are available");
        }

        // TODO: if somebody reserved this book they should get it first

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(libraryProperties.getLoanPeriodDays()));
        loan.setStatus(LoanStatus.ACTIVE);
        Loan saved = loanRepository.save(loan);

        log.debug("Member {} checked out book {}", member.getId(), book.getId());
        notificationService.sendCheckoutConfirmation(member.getEmail(), book.getTitle(), saved.getDueDate());

        return toResponse(saved);
    }

    @Transactional
    public LoanResponse returnBook(Long loanId) {
        Loan loan = getLoan(loanId);

        loan.setReturnDate(LocalDate.now());
        loan.setFineAmount(calculateFine(loan));
        loan.setStatus(LoanStatus.RETURNED);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        // book gets saved automatically because of @Transactional (i think)

        // TODO: tell ReservationService so the next person in the queue gets notified

        return toResponse(loanRepository.save(loan));
    }

    public LoanResponse renewLoan(Long loanId) {
        // TODO: renewals. rules: max shelfwise.max-renewals times, not allowed if the book is reserved
        throw new UnsupportedOperationException("Renewing loans is not implemented yet");
    }

    public Loan getLoan(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));
    }

    public List<LoanResponse> getLoansForMember(Long memberId) {
        return loanRepository.findByMemberId(memberId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LoanResponse> getOverdueLoans() {
        return loanRepository.findByDueDateBeforeAndStatus(LocalDate.now(), LoanStatus.ACTIVE).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Marks every active loan that is past its due date as OVERDUE.
     * Called by the nightly scheduler.
     *
     * @return the loans that were changed
     */
    @Transactional
    public List<Loan> markOverdueLoans() {
        List<Loan> loans = loanRepository.findByDueDateBeforeAndStatus(LocalDate.now(), LoanStatus.ACTIVE);
        for (Loan loan : loans) {
            loan.setStatus(LoanStatus.OVERDUE);
        }
        loanRepository.saveAll(loans);
        return loans;
    }

    /**
     * Calculates the fine for a loan. The member pays {@code shelfwise.fine-per-day}
     * for every day after the due date. If the book is not returned yet the fine
     * is calculated up to today.
     *
     * @param loan the loan
     * @return the fine, 0 if the book is not late
     */
    public double calculateFine(Loan loan) {
        LocalDate end = loan.getReturnDate() != null ? loan.getReturnDate() : LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), end);
        if (daysLate <= 0) {
            return 0;
        }
        // TODO: cap the fine at the price of the book?
        return daysLate * FINE_PER_DAY;
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getMember().getId(),
                loan.getMember().getName(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus().name(),
                loan.getFineAmount());
    }
}
