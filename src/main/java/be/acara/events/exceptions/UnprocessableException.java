package be.acara.events.exceptions;

import org.springframework.http.HttpStatus;

public class UnprocessableException extends CustomException {
    public UnprocessableException(String title, String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, title, message);
    }
}
