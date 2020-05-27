package be.acara.events.service.pdf;

import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.service.EventService;
import be.acara.events.testutil.EventUtil;
import be.acara.events.testutil.OrderUtil;
import be.acara.events.testutil.UserUtil;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {

    @Mock
    private EventService eventService;
    @Mock
    private QRCodeService qrCodeService;

    private PdfService pdfService;

    @BeforeEach
    void setUp() {
        pdfService = new PdfServiceImpl(eventService, qrCodeService);
    }

    @Test
    void createTicketPdf() throws Exception {
        CreateOrderList createOrderList = OrderUtil.createOrderList();
        User user = UserUtil.firstUser();
        Event event = EventUtil.firstEvent();

        when(eventService.findById(anyLong())).thenReturn(event);
        when(qrCodeService.getQRCodeImage(anyString(),anyInt(), anyInt())).thenReturn(null);

        byte[] ticketPdf = pdfService.createTicketPdf(createOrderList, user);

        assertThat(ticketPdf).isNotNull();
        assertThat(ticketPdf).hasSizeGreaterThan(0);
    }
}
