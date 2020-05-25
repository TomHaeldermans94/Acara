package be.acara.events.service.mail;

import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.User;

public interface MailService {
    /**
     * sends an email to the user with the created ticket (pdf) in attachment
     * @param createOrderList the list of orders of which a ticket has to be made
     * @param user the user that bought the order
     */
    void sendMessageWithAttachment(CreateOrderList createOrderList, User user);
}
