package com.shelfwise.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.BookDto;
import com.shelfwise.service.AuthService;
import com.shelfwise.service.RecommendationService;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final AuthService authService;

    public RecommendationController(RecommendationService recommendationService, AuthService authService) {
        this.recommendationService = recommendationService;
        this.authService = authService;
    }

    @GetMapping("/me")
    public List<BookDto> forMe(@RequestParam(defaultValue = "5") int limit) {
        return recommendationService.recommendForMember(authService.getCurrentMemberId(), limit);
    }

    @GetMapping("/member/{memberId}")
    public List<BookDto> forMember(@PathVariable Long memberId, @RequestParam(defaultValue = "5") int limit) {
        return recommendationService.recommendForMember(memberId, limit);
    }

    @GetMapping("/book/{bookId}/also-borrowed")
    public List<BookDto> alsoBorrowed(@PathVariable Long bookId, @RequestParam(defaultValue = "5") int limit) {
        return recommendationService.alsoBorrowed(bookId, limit);
    }
}
