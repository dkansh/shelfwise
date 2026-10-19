package com.shelfwise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shelfwise.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByBookIdAndStatusOrderByReservedAtAsc(Long bookId, String status);

    boolean existsByBookIdAndMemberIdAndStatus(Long bookId, Long memberId, String status);
}
