package com.shelfwise.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.BookDetailDto;
import com.shelfwise.dto.BookSearchCriteria;
import com.shelfwise.dto.PageResponse;
import com.shelfwise.service.BookSearchService;

/**
 * v2 of the books API with proper search + paging.
 * The v1 BookController is still used by the old frontend so we keep both for now.
 */
@RestController
@RequestMapping("/api/v2/books")
public class BookSearchController {

    private final BookSearchService searchService;

    public BookSearchController(BookSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookDetailDto>> search(@ModelAttribute BookSearchCriteria criteria,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size,
                                                              @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(searchService.search(criteria, page, size, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDto> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(searchService.getBookDetails(id));
    }
}
