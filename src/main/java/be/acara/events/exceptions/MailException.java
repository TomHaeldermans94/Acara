package be.acara.events.exceptions;

import org.springframework.http.HttpStatus;

public class MailException extends CustomException {

    public MailException(String title, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, title, message);
    }
}
