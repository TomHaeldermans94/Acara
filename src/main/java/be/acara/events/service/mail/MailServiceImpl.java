package be.acara.events.service.mail;

import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.User;
import be.acara.events.exceptions.MailException;
import be.acara.events.service.pdf.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;


@Component
public class MailServiceImpl implements MailService {

    private final JavaMailSender emailSender;
    private final PdfService pdfService;

    @Autowired
    public MailServiceImpl(JavaMailSender emailSender, PdfService pdfService) {
        this.emailSender = emailSender;
        this.pdfService = pdfService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessageWithAttachment(CreateOrderList createOrderList, User user) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject("Acara - Tickets");
            helper.setText("The tickets can be found in attachment");

            DataSource source = new ByteArrayDataSource(pdfService.createTicketPdf(createOrderList, user), "application/pdf");
            helper.addAttachment("Acara_tickets.pdf", source);

        } catch (MessagingException e) {
            throw new MailException("mailException", "Error with sending the email");
        }

        emailSender.send(message);

    }
}
