package be.acara.events.service.pdf;

import be.acara.events.domain.Order;
import be.acara.events.domain.User;

import java.util.List;

public interface PdfService {
    /**
     * Creates a ticket (pdf) in byte[] format of the order
     *
     * @param orderList the list of orders of which a ticket has to be made
     * @param user      the user that bought the order
     * @return byte[] ticketPdf a ticket (pdf) in byte[] format
     */
    byte[] createTicketPdf(List<Order> orderList, User user);
    
    byte[] createTicketPdf(Order order, User user);
}
