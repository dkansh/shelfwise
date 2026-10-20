package com.shelfwise.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.shelfwise.dto.BookDetailDto;
import com.shelfwise.dto.BookDto;
import com.shelfwise.model.Book;
import com.shelfwise.model.Category;

@Component
public class BookMapper {

    public BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedYear(),
                book.getGenre(),
                book.getTotalCopies(),
                book.getAvailableCopies());
    }

    public BookDetailDto toDetailDto(Book book, Double averageRating) {
        List<String> categoryNames = book.getCategories().stream()
                .map(Category::getName)
                .sorted()
                .toList();

        String authorName = book.getAuthorEntity() != null ? book.getAuthorEntity().getName() : book.getAuthor();
        Long authorId = book.getAuthorEntity() != null ? book.getAuthorEntity().getId() : null;

        return new BookDetailDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                authorName,
                authorId,
                book.getPublishedYear(),
                book.getGenre(),
                book.getLanguage(),
                book.getPageCount(),
                book.getDescription(),
                categoryNames,
                book.getTotalCopies(),
                book.getAvailableCopies(),
                averageRating);
    }
}
