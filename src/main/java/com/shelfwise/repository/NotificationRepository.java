package com.shelfwise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shelfwise.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<Notification> findByMemberIdAndReadFalse(Long memberId);

    long countByMemberIdAndReadFalse(Long memberId);
}
