package com.shelfwise.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Member;
import com.shelfwise.model.Notification;
import com.shelfwise.model.NotificationChannel;
import com.shelfwise.repository.NotificationRepository;
import com.shelfwise.service.notification.EmailSender;
import com.shelfwise.service.notification.EmailTemplateService;
import com.shelfwise.util.DateUtils;

/**
 * Sends notifications to members, by email and in the in-app inbox.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailSender emailSender;
    private final EmailTemplateService templateService;
    private final NotificationRepository notificationRepository;

    public NotificationService(EmailSender emailSender,
                               EmailTemplateService templateService,
                               NotificationRepository notificationRepository) {
        this.emailSender = emailSender;
        this.templateService = templateService;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Sends a confirmation after a member checks out a book.
     *
     * @param email     member email
     * @param bookTitle title of the book
     * @param dueDate   when the book has to be returned
     */
    public void sendCheckoutConfirmation(String email, String bookTitle, LocalDate dueDate) {
        String body = templateService.render("checkout",
                Map.of("title", bookTitle, "dueDate", DateUtils.format(dueDate)));
        sendEmailAsync(email, "You borrowed " + bookTitle, body);
    }

    public void sendOverdueReminder(String email, String bookTitle, double fine) {
        // TODO: use the overdue template like the other emails
        System.out.println("OVERDUE REMINDER -> " + email + ": '" + bookTitle
                + "' is overdue. Current fine: $" + fine);
    }

    public void sendReservationReady(Member member, String bookTitle) {
        createInApp(member.getId(), "Your reserved book is ready",
                "'" + bookTitle + "' is waiting for you at the front desk. You have 3 days to pick it up.");

        String body = templateService.render("reservation-ready",
                Map.of("name", member.getName(), "title", bookTitle));
        sendEmailAsync(member.getEmail(), "Your reserved book is ready: " + bookTitle, body);
    }

    public void sendMembershipExpiring(Member member, LocalDate expiryDate) {
        String body = templateService.render("membership-expiring",
                Map.of("name", member.getName(), "expiryDate", DateUtils.format(expiryDate)));
        sendEmailAsync(member.getEmail(), "Your Shelfwise membership is about to expire", body);
        createInApp(member.getId(), "Membership expiring", "Your membership expires on " + expiryDate);
    }

    public Notification createInApp(Long memberId, String title, String message) {
        Notification notification = new Notification();
        notification.setMemberId(memberId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setChannel(NotificationChannel.IN_APP);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public List<Notification> getForMember(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public long countUnread(Long memberId) {
        return notificationRepository.countByMemberIdAndReadFalse(memberId);
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(Long memberId) {
        List<Notification> unread = notificationRepository.findByMemberIdAndReadFalse(memberId);
        for (Notification n : unread) {
            n.setRead(true);
            notificationRepository.save(n);
        }
    }

    @Async
    public void sendEmailAsync(String to, String subject, String body) {
        try {
            emailSender.send(to, subject, body);
        } catch (Exception e) {
            logger.error("Could not send email to {}", to, e);
        }
    }
}
