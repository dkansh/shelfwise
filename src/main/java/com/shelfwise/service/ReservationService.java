package com.shelfwise.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Reservation;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.ReservationRepository;

/**
 * Lets members get in line for a book that is checked out.
 * <p>
 * WORK IN PROGRESS - only creating and listing reservations works right now.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
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

    public Reservation getNextInQueue(Long bookId) {
        List<Reservation> queue = getQueueForBook(bookId);
        if (queue.isEmpty()) {
            return null;
        }
        return queue.get(0);
    }

    public void cancelReservation(Long reservationId) {
        // FIXME: not done
        throw new UnsupportedOperationException("Cancelling reservations is not implemented yet");
    }
}
