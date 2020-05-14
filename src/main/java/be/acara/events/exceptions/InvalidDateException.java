package be.acara.events.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDateException extends CustomException{
    public InvalidDateException(String message) {
        super(HttpStatus.BAD_REQUEST, "Invalid date", message);
    }
}
