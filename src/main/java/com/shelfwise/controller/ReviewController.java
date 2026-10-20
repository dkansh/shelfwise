package com.shelfwise.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.ReviewDto;
import com.shelfwise.dto.ReviewRequest;
import com.shelfwise.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/books/{bookId}/reviews")
    public List<ReviewDto> getReviews(@PathVariable Long bookId) {
        return reviewService.getReviewsForBook(bookId);
    }

    @GetMapping("/books/{bookId}/rating")
    public Map<String, Object> getRating(@PathVariable Long bookId) {
        Map<String, Object> result = new HashMap<>();
        result.put("bookId", bookId);
        result.put("averageRating", reviewService.getAverageRating(bookId));
        return result;
    }

    @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long bookId, @Valid @RequestBody ReviewRequest request) {
        return new ResponseEntity<>(reviewService.addReview(bookId, request), HttpStatus.CREATED);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reviews/{id}/flag")
    public ReviewDto flag(@PathVariable Long id) {
        return reviewService.flagReview(id);
    }

    @GetMapping("/reviews/flagged")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public List<ReviewDto> flagged() {
        return reviewService.getFlaggedReviews();
    }
}
