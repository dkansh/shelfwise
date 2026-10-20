package com.shelfwise.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shelfwise.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByNameContainingIgnoreCase(String name);

    Member findByEmail(String email);

    List<Member> findByActiveTrue();

    long countByActiveTrue();

    List<Member> findByMembershipExpiryBetween(LocalDate from, LocalDate to);

    List<Member> findByMembershipExpiryBeforeAndActiveTrue(LocalDate date);
}
