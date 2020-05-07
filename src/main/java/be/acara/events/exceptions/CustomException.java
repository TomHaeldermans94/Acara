package be.acara.events.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final String title;
    private final String message;
    
    public CustomException(HttpStatus status, String title, String message) {
        super(message);
        this.status = status;
        this.title = title;
        this.message = message;
    }
}
