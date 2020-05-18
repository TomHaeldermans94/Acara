package be.acara.events.exceptions;

@SuppressWarnings("java:S110") // disabled because we know this won't go any deeper
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super("User not found", message);
    }
}
