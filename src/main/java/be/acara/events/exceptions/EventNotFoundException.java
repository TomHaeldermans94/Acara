package be.acara.events.exceptions;

@SuppressWarnings("java:S110") // disabled because we know this won't go any deeper
public class EventNotFoundException extends NotFoundException {
    public EventNotFoundException(String message) {
        super("Event not found", message);
    }
}
