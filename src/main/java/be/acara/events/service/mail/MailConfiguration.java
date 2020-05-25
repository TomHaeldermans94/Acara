package be.acara.events.service.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("classpath:email.properties")
public class MailConfiguration {

    @Value("${email}")
    private String myEmail;
    @Value("${password}")
    private String password;

    /**
     * Property resource configurer of the Spring framework that pulls values from a properties file into bean definitions
     * @return new PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Implementation of the JavaMailSender interface that provides functionality for sending emails
     * @return mailsender the mailsender with specifications of the Gmail SMTP server for sending emails
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(myEmail);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return mailSender;
    }
}
