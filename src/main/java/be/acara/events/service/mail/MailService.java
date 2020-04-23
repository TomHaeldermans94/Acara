package be.acara.events.service.mail;

import be.acara.events.domain.Event;

public interface MailService {
    void sendMessageWithAttachment(String recipient, Event event);
}
