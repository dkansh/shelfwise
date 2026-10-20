package com.shelfwise.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Used in dev - "sends" emails by writing them to the log.
 */
@Component
@ConditionalOnProperty(name = "shelfwise.notifications.email.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

    @Override
    public void send(String to, String subject, String body) {
        log.info("[EMAIL] to={} subject='{}'\n{}", to, subject, body);
    }
}
