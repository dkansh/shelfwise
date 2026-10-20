package com.shelfwise.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.shelfwise.service.ReservationService;

/**
 * When a book comes back, let the next person in the reservation queue know.
 */
@Component
public class ReservationEventListener {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventListener.class);

    private final ReservationService reservationService;

    public ReservationEventListener(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @EventListener
    public void onBookReturned(BookReturnedEvent event) {
        log.debug("Book {} was returned, checking the reservation queue", event.bookId());
        reservationService.notifyNextInQueue(event.bookId());
    }
}
