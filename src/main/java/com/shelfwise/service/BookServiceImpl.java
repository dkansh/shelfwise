package com.shelfwise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.dto.BookDto;
import com.shelfwise.dto.CreateBookRequest;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Book;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.util.IsbnValidator;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final AuditService auditService;

    public BookServiceImpl(BookRepository bookRepository, AuditService auditService) {
        this.bookRepository = bookRepository;
        this.auditService = auditService;
    }

    @Override
    public List<BookDto> getAllBooks() {
        // TODO: add pagination, this loads every single book
        return bookRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("books")
    public BookDto getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        return toDto(book);
    }

    @Override
    public List<BookDto> searchBooks(String title, String author) {
        // only one filter works at a time, title wins if both are sent
        if (title != null) {
            return bookRepository.findByTitleContainingIgnoreCase(title).stream().map(this::toDto).toList();
        } else if (author != null) {
            return bookRepository.findByAuthorContainingIgnoreCase(author).stream().map(this::toDto).toList();
        }
        return getAllBooks();
    }

    @Override
    public List<BookDto> getAvailableBooks() {
        return bookRepository.findAvailableBooks().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public BookDto createBook(CreateBookRequest request) {
        if (!IsbnValidator.isValid(request.isbn())) {
            throw new IllegalArgumentException("Invalid ISBN: " + request.isbn());
        }
        if (bookRepository.findByIsbn(request.isbn()).isPresent()) {
            throw new IllegalArgumentException("A book with ISBN " + request.isbn() + " already exists");
        }

        Book book = new Book();
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublishedYear(request.publishedYear());
        book.setGenre(request.genre());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.totalCopies());

        Book saved = bookRepository.save(book);
        log.info("Created book '{}' with id {}", saved.getTitle(), saved.getId());
        auditService.log("CREATE_BOOK", "Book", saved.getId(), saved.getTitle());
        return toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "books", key = "#id")
    public BookDto updateBook(Long id, CreateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublishedYear(request.publishedYear());
        book.setGenre(request.genre());
        // FIXME: availableCopies is not adjusted when totalCopies changes
        book.setTotalCopies(request.totalCopies());

        return toDto(bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long id) {
        // TODO: what if the book is checked out right now?
        bookRepository.deleteById(id);
        auditService.log("DELETE_BOOK", "Book", id, null);
    }

    private BookDto toDto(Book book) {
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
}
