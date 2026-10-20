package com.shelfwise.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shelfwise.dto.ImportResult;
import com.shelfwise.model.Book;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.util.CsvUtils;

/**
 * Bulk import of books from a CSV file.
 * Expected columns: isbn,title,author,year,genre,copies
 */
@Service
public class BookImportService {

    private static final Logger log = LoggerFactory.getLogger(BookImportService.class);

    private final BookRepository bookRepository;
    private final AuditService auditService;

    public BookImportService(BookRepository bookRepository, AuditService auditService) {
        this.bookRepository = bookRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ImportResult importBooks(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 || line.isBlank()) {
                    continue; // header
                }

                String[] cols = CsvUtils.parseLine(line);
                if (cols.length != 6) {
                    result.addError("Line " + lineNumber + ": expected 6 columns but got " + cols.length);
                    continue;
                }

                String isbn = cols[0].trim();
                if (bookRepository.findByIsbn(isbn).isPresent()) {
                    result.incrementSkipped();
                    continue;
                }

                try {
                    Book book = new Book();
                    book.setIsbn(isbn);
                    book.setTitle(cols[1].trim());
                    book.setAuthor(cols[2].trim());
                    book.setPublishedYear(Integer.parseInt(cols[3].trim()));
                    book.setGenre(cols[4].trim());
                    int copies = Integer.parseInt(cols[5].trim());
                    book.setTotalCopies(copies);
                    book.setAvailableCopies(copies);
                    bookRepository.save(book);
                    result.incrementImported();
                } catch (NumberFormatException e) {
                    result.addError("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        log.info("Imported {} books from {}", result.getImported(), file.getOriginalFilename());
        auditService.log("IMPORT_BOOKS", "Book", null,
                result.getImported() + " imported, " + result.getSkipped() + " skipped from " + file.getOriginalFilename());
        return result;
    }
}
