package be.acara.events.service;

import be.acara.events.domain.Event;
import be.acara.events.service.mail.MailService;
import be.acara.events.service.mail.MailServiceImpl;
import be.acara.events.service.pdf.PdfService;
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

import static be.acara.events.util.EventUtil.firstEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String recipient = "example@example.com";
        Event event = firstEvent();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService.sendMessageWithAttachment(recipient, event);
        verify(mailSender, times(1)).send(mimeMessage);
        assertEquals(recipient, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
    }

}
