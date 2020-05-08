package be.acara.events.exceptions;

public class InvalidYoutubeUrlException extends UnprocessableException {
    public InvalidYoutubeUrlException(String message) {
        super("Invalid youtube URL", message);
    }
}
