package be.acara.events.service.pdf;

import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.User;

public interface PdfService {
    /**
     * Creates a ticket (pdf) in byte[] format of the order
     * @param createOrderList the list of orders of which a ticket has to be made
     * @param user the user that bought the order
     * @return byte[] ticketPdf a ticket (pdf) in byte[] format
     */
    byte[] createTicketPdf(CreateOrderList createOrderList, User user);
}
