package be.acara.events.service;

import be.acara.events.domain.Order;
import be.acara.events.domain.User;
import be.acara.events.exceptions.MailException;
import be.acara.events.service.mail.MailService;
import be.acara.events.service.mail.MailServiceImpl;
import be.acara.events.service.pdf.PdfService;
import be.acara.events.testutil.OrderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.List;

import static be.acara.events.testutil.UserUtil.firstUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceUnitTest {

    @Mock
    private PdfService pdfService;
    @Mock
    private JavaMailSender mailSender;

    private MailService mailService;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage((Session)null);
        mailService = new MailServiceImpl(mailSender,pdfService);
    }
    
    @Test
    void sendMail() throws MessagingException {
        List<Order> orderList = OrderUtil.orderPage().getContent();
        User user = firstUser();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService.sendMessageWithAttachment(orderList, user);
        verify(mailSender, times(1)).send(mimeMessage);
        assertThat(user.getEmail()).isEqualTo(mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    void sendMail_error() {
        List<Order> orderList = OrderUtil.orderPage().getContent();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    
        User user = firstUser();
        user.setEmail("@@@@@@@@@@@@");
    
        assertThrows(MailException.class, () -> mailService.sendMessageWithAttachment(orderList, user));
    }
}
