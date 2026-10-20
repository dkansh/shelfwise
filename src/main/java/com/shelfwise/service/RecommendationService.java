package com.shelfwise.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shelfwise.dto.BookDto;
import com.shelfwise.mapper.BookMapper;
import com.shelfwise.model.Book;
import com.shelfwise.model.Loan;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.LoanRepository;
import com.shelfwise.repository.ReviewRepository;

/**
 * Simple "you might also like" recommendations.
 */
@Service
public class RecommendationService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final BookMapper bookMapper;

    public RecommendationService(LoanRepository loanRepository, BookRepository bookRepository,
                                 ReviewRepository reviewRepository, BookMapper bookMapper) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Recommends books in the genres the member reads the most, best rated first.
     * Books the member already borrowed are left out.
     */
    public List<BookDto> recommendForMember(Long memberId, int limit) {
        List<Loan> history = loanRepository.findByMemberId(memberId);
        if (history.isEmpty()) {
            // new member, just show the best rated books
            return topRated(bookRepository.findAll(), limit);
        }

        Set<Long> alreadyRead = history.stream().map(l -> l.getBook().getId()).collect(Collectors.toSet());
        Set<String> favouriteGenres = history.stream()
                .map(l -> l.getBook().getGenre())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Book> candidates = bookRepository.findAll().stream()
                .filter(b -> !alreadyRead.contains(b.getId()))
                .filter(b -> favouriteGenres.contains(b.getGenre()))
                .toList();

        return topRated(candidates, limit);
    }

    /**
     * "Members who borrowed this book also borrowed..."
     */
    public List<BookDto> alsoBorrowed(Long bookId, int limit) {
        List<Loan> allLoans = loanRepository.findAll();

        Set<Long> readers = allLoans.stream()
                .filter(l -> l.getBook().getId().equals(bookId))
                .map(l -> l.getMember().getId())
                .collect(Collectors.toSet());

        Map<Long, Integer> counts = new HashMap<>();
        for (Loan loan : allLoans) {
            Long otherBookId = loan.getBook().getId();
            if (readers.contains(loan.getMember().getId()) && !otherBookId.equals(bookId)) {
                counts.merge(otherBookId, 1, Integer::sum);
            }
        }

        return counts.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(e -> bookRepository.findById(e.getKey()).get())
                .map(bookMapper::toDto)
                .toList();
    }

    private List<BookDto> topRated(List<Book> books, int limit) {
        return books.stream()
                .sorted(Comparator.comparing((Book b) -> rating(b.getId())).reversed())
                .limit(limit)
                .map(bookMapper::toDto)
                .toList();
    }

    private double rating(Long bookId) {
        Double avg = reviewRepository.findAverageRating(bookId);
        return avg == null ? 0 : avg;
    }
}
