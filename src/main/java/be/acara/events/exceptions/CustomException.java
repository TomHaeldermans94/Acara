package be.acara.events.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private HttpStatus status;
    private String title;
    private String message;
    
    public CustomException(HttpStatus status, String title, String message) {
        super(message);
        this.status = status;
        this.title = title;
        this.message = message;
    }
}
