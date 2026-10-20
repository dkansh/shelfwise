package com.shelfwise.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.dto.BookCopyDto;
import com.shelfwise.dto.CreateCopyRequest;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Book;
import com.shelfwise.model.BookCopy;
import com.shelfwise.model.CopyCondition;
import com.shelfwise.model.CopyStatus;
import com.shelfwise.model.LibraryBranch;
import com.shelfwise.repository.BookCopyRepository;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.LibraryBranchRepository;

/**
 * Manages the physical copies of books.
 * <p>
 * NOTE: Book.totalCopies / availableCopies are the old counters, we try to keep them in sync here.
 */
@Service
public class BookCopyService {

    private final BookCopyRepository copyRepository;
    private final BookRepository bookRepository;
    private final LibraryBranchRepository branchRepository;
    private final Random random = new Random();

    public BookCopyService(BookCopyRepository copyRepository, BookRepository bookRepository,
                           LibraryBranchRepository branchRepository) {
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
        this.branchRepository = branchRepository;
    }

    @Transactional
    public BookCopyDto addCopy(CreateCopyRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.getBookId()));
        LibraryBranch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + request.getBranchId()));

        BookCopy copy = new BookCopy();
        copy.setBarcode(generateBarcode());
        copy.setBook(book);
        copy.setBranch(branch);
        copy.setCondition(request.getCondition() != null ? request.getCondition() : CopyCondition.NEW);
        copy.setStatus(CopyStatus.AVAILABLE);
        copy.setAcquiredDate(LocalDate.now());
        copy.setPrice(request.getPrice());
        copy.setNotes(request.getNotes());
        copyRepository.save(copy);

        // keep the old counters in sync
        book.setTotalCopies(book.getTotalCopies() + 1);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return toDto(copy);
    }

    public List<BookCopyDto> getCopiesForBook(Long bookId) {
        return copyRepository.findByBookId(bookId).stream().map(this::toDto).toList();
    }

    public List<BookCopyDto> getCopiesAtBranch(Long branchId) {
        return copyRepository.findByBranchId(branchId).stream().map(this::toDto).toList();
    }

    public BookCopyDto findByBarcode(String barcode) {
        BookCopy copy = copyRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("No copy with barcode " + barcode));
        return toDto(copy);
    }

    @Transactional
    public BookCopyDto updateCondition(Long copyId, CopyCondition condition) {
        BookCopy copy = getCopy(copyId);
        copy.setCondition(condition);
        return toDto(copyRepository.save(copy));
    }

    @Transactional
    public void withdrawCopy(Long copyId) {
        BookCopy copy = getCopy(copyId);
        copy.setStatus(CopyStatus.WITHDRAWN);
        copyRepository.save(copy);
        // FIXME: book.totalCopies / availableCopies are not updated here
    }

    @Transactional
    public void markLost(Long copyId) {
        BookCopy copy = getCopy(copyId);
        copy.setStatus(CopyStatus.LOST);
        copyRepository.save(copy);

        Book book = copy.getBook();
        book.setTotalCopies(book.getTotalCopies() - 1);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    private BookCopy getCopy(Long copyId) {
        return copyRepository.findById(copyId)
                .orElseThrow(() -> new ResourceNotFoundException("Copy not found: " + copyId));
    }

    private String generateBarcode() {
        // TODO: could collide with an existing barcode, should check the db first
        return "SW-" + String.format("%06d", random.nextInt(1_000_000));
    }

    private BookCopyDto toDto(BookCopy copy) {
        return new BookCopyDto(
                copy.getId(),
                copy.getBarcode(),
                copy.getBook().getId(),
                copy.getBook().getTitle(),
                copy.getBranch() != null ? copy.getBranch().getCode() : null,
                copy.getCondition(),
                copy.getStatus(),
                copy.getAcquiredDate());
    }
}
