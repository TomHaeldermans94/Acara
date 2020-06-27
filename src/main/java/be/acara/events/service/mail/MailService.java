package be.acara.events.service.mail;

import be.acara.events.domain.Order;
import be.acara.events.domain.User;

import java.util.List;

public interface MailService {
    /**
     * sends an email to the user with the created ticket (pdf) in attachment
     *
     * @param orderList the list of orders of which a ticket has to be made
     * @param user      the user that bought the order
     */
    void sendMessageWithAttachment(List<Order> orderList, User user);
}
