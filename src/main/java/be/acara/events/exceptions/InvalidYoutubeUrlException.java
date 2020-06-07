package be.acara.events.exceptions;

@SuppressWarnings("java:S110") // disabled because we know this won't go any deeper
public class InvalidYoutubeUrlException extends UnprocessableException {
    public InvalidYoutubeUrlException(String message) {
        super("Invalid youtube URL", message);
    }
}