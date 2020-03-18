package be.acara.events.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomException {

    public BadRequestException(String message, String title) {
        super(HttpStatus.BAD_REQUEST, title, message);
    }
}
