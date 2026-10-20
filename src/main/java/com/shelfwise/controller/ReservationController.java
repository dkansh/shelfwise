package com.shelfwise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.model.Reservation;
import com.shelfwise.service.ReservationService;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> reserve(@RequestParam Long bookId, @RequestParam Long memberId) {
        Reservation reservation = reservationService.reserveBook(bookId, memberId);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @GetMapping("/book/{bookId}")
    public List<Reservation> getQueue(@PathVariable Long bookId) {
        return reservationService.getQueueForBook(bookId);
    }

    @GetMapping("/member/{memberId}")
    public List<Reservation> getForMember(@PathVariable Long memberId) {
        return reservationService.getReservationsForMember(memberId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
