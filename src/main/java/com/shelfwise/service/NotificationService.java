package com.shelfwise.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shelfwise.util.DateUtils;

/**
 * Sends notifications to members.
 * <p>
 * NOTE: nothing is actually emailed yet, everything just goes to the log.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Sends a confirmation after a member checks out a book.
     *
     * @param email   member email
     * @param bookTitle title of the book
     * @param dueDate when the book has to be returned
     */
    public void sendCheckoutConfirmation(String email, String bookTitle, LocalDate dueDate) {
        // TODO: use JavaMailSender (needs spring-boot-starter-mail + smtp settings)
        logger.info("[EMAIL] to={} : You borrowed '{}'. Please return it by {}",
                email, bookTitle, DateUtils.format(dueDate));
    }

    public void sendOverdueReminder(String email, String bookTitle, double fine) {
        System.out.println("OVERDUE REMINDER -> " + email + ": '" + bookTitle
                + "' is overdue. Current fine: $" + fine);
    }

    // TODO: sendReservationReady(...) for when a reserved book comes back
}
