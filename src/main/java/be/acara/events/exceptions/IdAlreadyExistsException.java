package be.acara.events.exceptions;

@SuppressWarnings("java:S110") // disabled because we know this won't go any deeper
public class IdAlreadyExistsException extends UnprocessableException {
    public IdAlreadyExistsException(String message) {
        super("Cannot process entry", message);
    }
}
