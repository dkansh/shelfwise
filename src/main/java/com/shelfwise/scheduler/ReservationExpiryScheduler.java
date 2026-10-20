package com.shelfwise.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shelfwise.service.ReservationService;

@Component
public class ReservationExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpiryScheduler.class);

    @Autowired
    private ReservationService reservationService;

    // every hour
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void expireReservations() {
        int count = reservationService.expireOldReservations();
        if (count > 0) {
            log.info("Expired {} reservations that were not picked up", count);
        }
    }
}
