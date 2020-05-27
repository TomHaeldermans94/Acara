package be.acara.events.exceptions;

import org.springframework.http.HttpStatus;

public class QRException extends CustomException{
    public QRException(String title, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, title, message);
    }
}
