package be.acara.events.exceptions;

public class IdAlreadyExistsException extends UnprocessableException {
    public IdAlreadyExistsException(String message) {
        super("Cannot process entry", message);
    }
}
