package com.shelfwise.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.shelfwise.model.Book;
import com.shelfwise.model.Loan;
import com.shelfwise.model.LoanStatus;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.LoanRepository;
import com.shelfwise.util.CsvUtils;

/**
 * CSV exports for the librarians (they open them in Excel).
 */
@Service
public class ExportService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public ExportService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    public String exportBooksCsv() {
        StringBuilder sb = new StringBuilder("id,isbn,title,author,year,genre,total_copies,available_copies\n");
        for (Book book : bookRepository.findAll()) {
            sb.append(CsvUtils.toLine(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(),
                    book.getPublishedYear(), book.getGenre(), book.getTotalCopies(), book.getAvailableCopies()));
            sb.append("\n");
        }
        return sb.toString();
    }

    public String exportOverdueLoansCsv() {
        StringBuilder sb = new StringBuilder("loan_id,book_title,member_name,member_email,due_date,days_overdue\n");
        for (Loan loan : loanRepository.findByDueDateBeforeAndStatus(LocalDate.now(), LoanStatus.ACTIVE)) {
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
            sb.append(loan.getId()).append(",")
                    .append(loan.getBook().getTitle()).append(",")
                    .append(loan.getMember().getName()).append(",")
                    .append(loan.getMember().getEmail()).append(",")
                    .append(loan.getDueDate()).append(",")
                    .append(daysOverdue).append("\n");
        }
        return sb.toString();
    }
}
