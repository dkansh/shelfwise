package com.shelfwise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shelfwise.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookIdOrderByCreatedAtDesc(Long bookId);

    List<Review> findByFlaggedTrue();

    boolean existsByBookIdAndMemberId(Long bookId, Long memberId);

    @Query("select avg(r.rating) from Review r where r.book.id = :bookId")
    Double findAverageRating(@Param("bookId") Long bookId);
}
