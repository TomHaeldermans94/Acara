package be.acara.events.exceptions;

@SuppressWarnings("java:S110") // disabled because we know this won't go any deeper
public class IdNotFoundException extends NotFoundException{
    public IdNotFoundException(String message) {
        super("Cannot process entry", message);
    }
}
