package be.acara.events.service.mail;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.service.pdf.EventPdf;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class MailService {

    private static final String myEmail = "tickets.acara@gmail.com";
    private static final String password = "Acara123";

    private static Session createSession() throws GeneralSecurityException {
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        Properties properties = new Properties();
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myEmail, password);
            }
        });
    }

    public static void sendMail(String recipient, Event event) {
        try {
            Session session = createSession();
            Message message = prepareMessageNewTicket(session, recipient, event);
            if (message != null) {
                Transport.send(message);
            }
        } catch (GeneralSecurityException | MessagingException e) {
            e.printStackTrace();
        }
    }

    private static Message prepareMessageNewTicket(Session session, String recipient, Event event) {
        Message message = null;
        if (!recipient.equals("")) {
            try {
                String[] recipients = recipient.split(",");
                message = new MimeMessage(session);
                message.setFrom(new InternetAddress(myEmail));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients[0]));
                for (int i = 1; i < recipients.length; i++) {
                    message.setRecipient(Message.RecipientType.CC, new InternetAddress(recipients[i]));
                }
                message.setSubject(String.format("Acara - Ticket - %s", event.getName()));
                Multipart multipart = getAttachmentForEmail(event);
                message.setContent(multipart);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    private static Multipart getAttachmentForEmail(Event event){
        Multipart multipart = new MimeMultipart();
        try {
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(String.format("The ticket for %s can be found in attachment", event.getName()));
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();

            DataSource source = new ByteArrayDataSource(EventPdf.createTicketPdf(event, new User(5L, "tom", "haeldermans", null, "tom", "tompw", null, "tomhaeldermans94@gmail.com")), "application/pdf"); // ex : "C:\\test.pdf"
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(getFileNameFromEvent(event)); // ex : "test.pdf"
            multipart.addBodyPart(textBodyPart);  // add the text part
            multipart.addBodyPart(attachmentBodyPart); // add the attachement part
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return multipart;
    }

    private static String getFileNameFromEvent(Event event) {
        return String.format("Acara_%s_%s.pdf",event.getId().toString(),event.getName());
    }
}
