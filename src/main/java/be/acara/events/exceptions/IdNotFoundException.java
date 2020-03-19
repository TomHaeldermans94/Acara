package be.acara.events.exceptions;

public class IdNotFoundException extends NotFoundException{
    public IdNotFoundException(String message) {
        super("Cannot process entry", message);
    }
}
