package be.acara.events.service.mail;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;

public interface MailService {
    void sendMessageWithAttachment(Event event, User user);
}
