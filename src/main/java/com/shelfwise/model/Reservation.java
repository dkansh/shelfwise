package com.shelfwise.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long bookId;

    private Long memberId;

    private LocalDateTime reservedAt;

    // PENDING, READY, FULFILLED, CANCELLED, EXPIRED
    private String status;

    // set when the book comes back and the member is told to pick it up
    private LocalDateTime notifiedAt;

    private LocalDateTime expiresAt;
}
