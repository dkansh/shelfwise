package com.shelfwise.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Book;
import com.shelfwise.model.Member;
import com.shelfwise.model.Reservation;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.repository.ReservationRepository;

/**
 * Lets members get in line for a book that is checked out.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    public ReservationService(ReservationRepository reservationRepository,
                              BookRepository bookRepository,
                              MemberRepository memberRepository,
                              NotificationService notificationService) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
    }

    public Reservation reserveBook(Long bookId, Long memberId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found: " + bookId);
        }
        // TODO: check the member exists
        // TODO: you shouldn't be able to reserve a book that has copies available
        // TODO: stop the same member reserving the same book twice

        Reservation reservation = new Reservation();
        reservation.setBookId(bookId);
        reservation.setMemberId(memberId);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setStatus("PENDING");
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getQueueForBook(Long bookId) {
        return reservationRepository.findByBookIdAndStatusOrderByReservedAtAsc(bookId, "PENDING");
    }

    public List<Reservation> getReservationsForMember(Long memberId) {
        return reservationRepository.findByMemberId(memberId);
    }

    public Reservation getNextInQueue(Long bookId) {
        List<Reservation> queue = getQueueForBook(bookId);
        if (queue.isEmpty()) {
            return null;
        }
        return queue.get(0);
    }

    /**
     * Called when a copy of the book comes back. Moves the first person in the
     * queue to READY and tells them to come and pick it up.
     */
    @Transactional
    public void notifyNextInQueue(Long bookId) {
        Reservation next = getNextInQueue(bookId);
        if (next == null) {
            return;
        }

        next.setStatus("READY");
        next.setNotifiedAt(LocalDateTime.now());
        next.setExpiresAt(LocalDateTime.now().plusDays(3));
        reservationRepository.save(next);

        Member member = memberRepository.findById(next.getMemberId()).get();
        Book book = bookRepository.findById(bookId).get();
        notificationService.sendReservationReady(member, book.getTitle());
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));
        reservation.setStatus("CANCELED");
        reservationRepository.save(reservation);
        // TODO: if it was READY we should move on to the next person
    }

    /**
     * READY reservations that nobody picked up in time are expired and the book goes
     * to the next person in line.
     *
     * @return how many reservations were expired
     */
    @Transactional
    public int expireOldReservations() {
        List<Reservation> expired = reservationRepository.findByStatusAndExpiresAtBefore("READY", LocalDateTime.now());
        for (Reservation reservation : expired) {
            reservation.setStatus("EXPIRED");
            reservationRepository.save(reservation);
            notifyNextInQueue(reservation.getBookId());
        }
        return expired.size();
    }
}
