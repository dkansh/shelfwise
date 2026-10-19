package com.shelfwise.service;

import java.util.List;

import com.shelfwise.dto.BookDto;
import com.shelfwise.dto.CreateBookRequest;

public interface BookService {

    List<BookDto> getAllBooks();

    BookDto getBook(Long id);

    List<BookDto> searchBooks(String title, String author);

    List<BookDto> getAvailableBooks();

    BookDto createBook(CreateBookRequest request);

    BookDto updateBook(Long id, CreateBookRequest request);

    void deleteBook(Long id);
}
