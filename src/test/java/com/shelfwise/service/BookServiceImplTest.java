package com.shelfwise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shelfwise.dto.BookDto;
import com.shelfwise.dto.CreateBookRequest;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Book;
import com.shelfwise.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void createBook_savesAndReturnsDto() {
        CreateBookRequest request = new CreateBookRequest("9780132350884", "Clean Code",
                "Robert C. Martin", 2008, "Programming", 3);
        when(bookRepository.findByIsbn("9780132350884")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookDto result = bookService.createBook(request);

        assertEquals(1L, result.id());
        assertEquals("Clean Code", result.title());
        assertEquals(3, result.availableCopies());
    }

    @Test
    void createBook_rejectsDuplicateIsbn() {
        CreateBookRequest request = new CreateBookRequest("9780132350884", "Clean Code",
                "Robert C. Martin", 2008, "Programming", 3);
        when(bookRepository.findByIsbn("9780132350884")).thenReturn(Optional.of(new Book()));

        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(request));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void createBook_rejectsInvalidIsbn() {
        CreateBookRequest request = new CreateBookRequest("12345", "Bad", "Nobody", 2020, null, 1);

        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(request));
    }

    @Test
    void getBook_throwsWhenMissing() {
        when(bookRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBook(42L));
    }
}
