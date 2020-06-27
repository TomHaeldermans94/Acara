package be.acara.events.exceptions;

import org.springframework.http.HttpStatus;

public class PdfException extends CustomException {

    public PdfException(String title, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, title, message);
    }
}
