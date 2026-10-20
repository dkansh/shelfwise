package com.shelfwise.events;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    List<EventRegistration> findByEventId(Long eventId);

    long countByEventId(Long eventId);

    List<EventRegistration> findByMemberId(Long memberId);

    Optional<EventRegistration> findByEventIdAndMemberId(Long eventId, Long memberId);
}
