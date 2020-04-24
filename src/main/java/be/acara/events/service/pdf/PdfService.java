package be.acara.events.service.pdf;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;

public interface PdfService {
    byte[] createTicketPdf(Event event, User user);
}
