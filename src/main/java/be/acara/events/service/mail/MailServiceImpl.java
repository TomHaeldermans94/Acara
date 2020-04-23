package be.acara.events.service.mail;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.service.pdf.EventPdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;


@Component
public class MailServiceImpl {

    @Autowired
    @Qualifier("getJavaMailSender")
    public JavaMailSender emailSender;

    public void sendMessageWithAttachment(String recipient, Event event) {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);

            helper.setTo(recipient);
            helper.setSubject(String.format("Acara - Ticket - %s", event.getName()));
            helper.setText(String.format("The ticket for %s can be found in attachment", event.getName()));

            DataSource source = new ByteArrayDataSource(EventPdf.createTicketPdf(event, new User(5L, "tom", "haeldermans", null, "tom", "tompw", null, "tomhaeldermans94@gmail.com")), "application/pdf"); // ex : "C:\\test.pdf"
            helper.addAttachment(getFileNameFromEvent(event), source);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        emailSender.send(message);

    }

    private String getFileNameFromEvent(Event event) {
        return String.format("Acara_%s_%s.pdf", event.getId().toString(), event.getName());
    }
}
