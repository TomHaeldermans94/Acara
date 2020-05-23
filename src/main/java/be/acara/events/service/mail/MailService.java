package be.acara.events.service.mail;

import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;

import java.util.List;

public interface MailService {
    void sendMessageWithAttachment(CreateOrderList createOrderList, User user);
}
