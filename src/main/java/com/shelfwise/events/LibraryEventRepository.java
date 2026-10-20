package com.shelfwise.events;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryEventRepository extends JpaRepository<LibraryEvent, Long> {

    List<LibraryEvent> findByStartTimeAfterAndStatusOrderByStartTimeAsc(LocalDateTime time, EventStatus status);

    List<LibraryEvent> findByBranchId(Long branchId);
}
