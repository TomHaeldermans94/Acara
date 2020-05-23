package be.acara.events.service.pdf;

import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.service.EventService;
import be.acara.events.service.UserServiceImpl;
import be.acara.events.testutil.EventUtil;
import be.acara.events.testutil.OrderUtil;
import be.acara.events.testutil.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {

    @Mock
    private EventService eventService;

    private PdfService pdfService;

    @BeforeEach
    void setUp() {
        pdfService = new PdfServiceImpl(eventService);
    }
    
    @Test
    void createTicketPdf() {
        CreateOrderList createOrderList = OrderUtil.createOrderList();
        User user = UserUtil.firstUser();
        Event event = EventUtil.firstEvent();

        byte[] ticketPdf = pdfService.createTicketPdf(createOrderList, user);

        when(eventService.findById(anyLong())).thenReturn(event);

        assertThat(ticketPdf).isNotNull();
        assertThat(ticketPdf).hasSizeGreaterThan(0);
    }
}
