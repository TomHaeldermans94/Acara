package be.acara.events.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {
    public NotFoundException(String title, String message) {
        super(HttpStatus.NOT_FOUND, title, message);
    }
}
