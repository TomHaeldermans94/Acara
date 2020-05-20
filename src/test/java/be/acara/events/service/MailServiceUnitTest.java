package be.acara.events.service;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.exceptions.MailException;
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

import static be.acara.events.testutil.EventUtil.firstEvent;
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
        Event event = firstEvent();
        User user = firstUser();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService.sendMessageWithAttachment(event, user);
        verify(mailSender, times(1)).send(mimeMessage);
        assertThat(user.getEmail()).isEqualTo(mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
    }
    
    @Test
    void sendMail_error() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        User user = firstUser();
        user.setEmail("@@@@@@@@@@@@");
        
        assertThrows(MailException.class, () -> mailService.sendMessageWithAttachment(firstEvent(), user));
    }
}
