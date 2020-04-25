package be.acara.events.service.mail;

import be.acara.events.domain.Event;
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
public class MailServiceImpl implements MailService{


    private final JavaMailSender emailSender;
    private final PdfService pdfService;

    @Autowired
    public MailServiceImpl(JavaMailSender emailSender, PdfService pdfService) {
        this.emailSender = emailSender;
        this.pdfService = pdfService;
    }

    public void sendMessageWithAttachment(Event event, User user) {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject(String.format("Acara - Ticket - %s", event.getName()));
            helper.setText(String.format("The ticket for %s can be found in attachment", event.getName()));

            DataSource source = new ByteArrayDataSource(pdfService.createTicketPdf(event, user), "application/pdf");
            helper.addAttachment(getFileNameFromEvent(event), source);

        } catch (MessagingException e) {
            throw new MailException("mailException", "Error with sending the email");
        }

        emailSender.send(message);

    }

    private String getFileNameFromEvent(Event event) {
        return String.format("Acara_%s_%s.pdf", event.getId().toString(), event.getName());
    }
}
