package com.shelfwise.service.notification;

public interface EmailSender {

    void send(String to, String subject, String body);
}
