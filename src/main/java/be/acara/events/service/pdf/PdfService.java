package be.acara.events.service.pdf;

import be.acara.events.domain.Order;

import java.util.List;

public interface PdfService {
    /**
     * Creates a ticket (pdf) in byte[] format of the order
     *
     * @param orderList the list of orders of which a ticket has to be made
     * @return byte[] ticketPdf a ticket (pdf) in byte[] format
     */
    byte[] createTicketPdf(List<Order> orderList);
    
    /**
     * Puts the single order object in a singletonList and passes it on to {@link #createTicketPdf(List)}
     *
     * @param order the order to get a ticket from
     */
    byte[] createTicketPdf(Order order);
}
