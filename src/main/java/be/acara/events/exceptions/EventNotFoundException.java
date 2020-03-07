package be.acara.events.exceptions;

public class EventNotFoundException extends NotFoundException {
    public EventNotFoundException(String message) {
        super("Event not found", message);
    }
}
