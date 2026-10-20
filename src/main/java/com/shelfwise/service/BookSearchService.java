package com.shelfwise.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.dto.BookDetailDto;
import com.shelfwise.dto.BookSearchCriteria;
import com.shelfwise.dto.PageResponse;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.mapper.BookMapper;
import com.shelfwise.model.Book;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.ReviewRepository;
import com.shelfwise.repository.spec.BookSpecifications;
import com.shelfwise.util.StringUtils;

/**
 * Search used by the v2 API. Supports filtering, paging and sorting.
 */
@Service
@Transactional(readOnly = true)
public class BookSearchService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final BookMapper bookMapper;

    public BookSearchService(BookRepository bookRepository, ReviewRepository reviewRepository,
                             BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.bookMapper = bookMapper;
    }

    public PageResponse<BookDetailDto> search(BookSearchCriteria criteria, int page, int size, String sort) {
        List<Specification<Book>> specs = new ArrayList<>();
        if (!StringUtils.isBlank(criteria.getQ())) {
            specs.add(BookSpecifications.titleOrDescriptionContains(criteria.getQ()));
        }
        if (!StringUtils.isBlank(criteria.getAuthor())) {
            specs.add(BookSpecifications.authorContains(criteria.getAuthor()));
        }
        if (!StringUtils.isBlank(criteria.getCategory())) {
            specs.add(BookSpecifications.inCategory(criteria.getCategory()));
        }
        if (!StringUtils.isBlank(criteria.getLanguage())) {
            specs.add(BookSpecifications.hasLanguage(criteria.getLanguage()));
        }
        if (Boolean.TRUE.equals(criteria.getAvailableOnly())) {
            specs.add(BookSpecifications.availableOnly());
        }
        if (criteria.getYearFrom() != null) {
            specs.add(BookSpecifications.publishedAfter(criteria.getYearFrom()));
        }
        if (criteria.getYearTo() != null) {
            specs.add(BookSpecifications.publishedBefore(criteria.getYearTo()));
        }

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Book> result = bookRepository.findAll(Specification.allOf(specs), pageable);

        return PageResponse.of(result.map(book ->
                bookMapper.toDetailDto(book, reviewRepository.findAverageRating(book.getId()))));
    }

    public BookDetailDto getBookDetails(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        return bookMapper.toDetailDto(book, reviewRepository.findAverageRating(id));
    }

    // "title" or "title,desc"
    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("title");
        }
        String[] parts = sort.split(",");
        Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
        return Sort.by(direction, parts[0]);
    }
}
