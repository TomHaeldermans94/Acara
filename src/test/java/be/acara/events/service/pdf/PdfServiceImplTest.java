package be.acara.events.service.pdf;

import be.acara.events.domain.Order;
import be.acara.events.domain.User;
import be.acara.events.testutil.OrderUtil;
import be.acara.events.testutil.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {
    @Mock
    private QRCodeService qrCodeService;

    private PdfService pdfService;

    @BeforeEach
    void setUp() {
        pdfService = new PdfServiceImpl(qrCodeService);
    }

    @Test
    void createTicketPdf() throws Exception {
        List<Order> orderList = OrderUtil.orderPage().getContent();
        User user = UserUtil.firstUser();
    
        when(qrCodeService.getQRCodeImage(anyString(), anyInt(), anyInt())).thenReturn(null);
    
        byte[] ticketPdf = pdfService.createTicketPdf(orderList, user);
    
        assertThat(ticketPdf).isNotNull();
        assertThat(ticketPdf).hasSizeGreaterThan(0);
    }
}
