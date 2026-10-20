package com.shelfwise.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.dto.ReviewDto;
import com.shelfwise.dto.ReviewRequest;
import com.shelfwise.exception.DuplicateResourceException;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Book;
import com.shelfwise.model.Member;
import com.shelfwise.model.Review;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository,
                         MemberRepository memberRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ReviewDto addReview(Long bookId, ReviewRequest request) {
        if (request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + request.memberId()));

        if (reviewRepository.existsByBookIdAndMemberId(bookId, member.getId())) {
            throw new DuplicateResourceException("You already reviewed this book");
        }
        // TODO: only members who actually borrowed the book should be able to review it

        Review review = new Review();
        review.setBook(book);
        review.setMember(member);
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setCreatedAt(LocalDateTime.now());
        review.setFlagged(false);
        return toDto(reviewRepository.save(review));
    }

    public List<ReviewDto> getReviewsForBook(Long bookId) {
        return reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId).stream()
                .filter(r -> !r.isFlagged())
                .map(this::toDto)
                .toList();
    }

    public Double getAverageRating(Long bookId) {
        return reviewRepository.findAverageRating(bookId);
    }

    public void deleteReview(Long reviewId) {
        // TODO: members should only be able to delete their own reviews
        reviewRepository.deleteById(reviewId);
    }

    public ReviewDto flagReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        review.setFlagged(true);
        return toDto(reviewRepository.save(review));
    }

    public List<ReviewDto> getFlaggedReviews() {
        return reviewRepository.findByFlaggedTrue().stream().map(this::toDto).toList();
    }

    private ReviewDto toDto(Review r) {
        return new ReviewDto(r.getId(), r.getBook().getId(), r.getMember().getId(), r.getMember().getName(),
                r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
