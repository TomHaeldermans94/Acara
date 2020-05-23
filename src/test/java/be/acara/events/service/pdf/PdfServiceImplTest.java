package be.acara.events.service.pdf;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.testutil.EventUtil;
import be.acara.events.testutil.UserUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PdfServiceImplTest {
    
    @Test
    void createTicketPdf() {
        Event event = EventUtil.firstEvent();
        User user = UserUtil.firstUser();
        
        PdfServiceImpl pdfService = new PdfServiceImpl();
        byte[] ticketPdf = pdfService.createTicketPdf(event, user);
        
        assertThat(ticketPdf).isNotNull();
        assertThat(ticketPdf).hasSizeGreaterThan(0);
    }
}
